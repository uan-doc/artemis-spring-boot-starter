package br.com.uan.artemis;

import java.security.InvalidParameterException;

import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

//Não deve ser um componente ou service, deve ser instanciado manualmente como no ArtemisAutoConfiguration.java
//@Component
public class QueueService
{
    private final Logger logger = LoggerFactory.getLogger(QueueService.class);

    private JmsTemplate  jmsTemplate;
    private JmsTemplate  secondaryJmsTemplate;
    private ObjectMapper objectMapperForBroker;

    public QueueService(JmsTemplate jmsTemplate, JmsTemplate secondaryJmsTemplate, ObjectMapper objectMapperForBroker) {
        this.jmsTemplate = jmsTemplate;
        this.secondaryJmsTemplate = secondaryJmsTemplate;
        this.objectMapperForBroker = objectMapperForBroker;
    }

    /**
     * Converte a mensagem e a envia para a fila informada.
     * 
     * @param priority
     *            a prioridade da mensagem. Deve ser um valor entre 0 e 9, onde
     *            0 é mais baixa e a 9 é a mais alta, ou seja, as mensagens com
     *            prioridade 9 serão atendidas primeiramente.
     */
    public void convertAndSend(String queueName, Object message, int userIdToFilterMessagesByUser) throws Exception {
        convertAndSend(queueName, message, 4, userIdToFilterMessagesByUser);
    }

    public void convertAndSend(String queueName, Object message, int priority, int userIdToFilterMessagesByUser) throws Exception {
        convertAndSend(queueName, message, priority, false, userIdToFilterMessagesByUser);
    }

    public void convertAndSend(String queueName, Object message, int priority, boolean serializeInJson, int userIdToFilterMessagesByUser)
            throws Exception {
        convertAndSend(queueName, message, priority, serializeInJson, false, userIdToFilterMessagesByUser);
    }

    public void convertAndSend(String queueName, Object message, int priority, boolean serializeInJson, boolean useSecondaryJmsTemplate,
            int userIdToFilterMessagesByUser) throws Exception {
        JmsTemplate currentJmsTemplate = jmsTemplate;

        if (useSecondaryJmsTemplate) {
            currentJmsTemplate = secondaryJmsTemplate;
        }

        if (priority < 0) {
            priority = 4;
        }

        if (priority != 4 && !currentJmsTemplate.isExplicitQosEnabled()) {
            throw new InvalidParameterException("Configure a propriedade '"
                    + (useSecondaryJmsTemplate ? "spring.jms.template.secondary-qos-enabled" : "spring.jms.template.qos-enabled")
                    + " = true' no 'application.properties' para conseguir enviar mensagens com prioridade.");
        }

        currentJmsTemplate.setPriority(priority);
        currentJmsTemplate.setMessageIdEnabled(false);
        currentJmsTemplate.setMessageTimestampEnabled(false);

        // Serializando em JSon. Isso é comumente utilizado no Web Socket, pois
        // o JavaScript consome a fila do Broker diretamente.
        if (serializeInJson && objectMapperForBroker != null) {
            message = objectMapperForBroker.writeValueAsString(message);
        }

        logger.debug("Enviando a mensagem '{}' para a fila '{}' com a prioridade '{}' com o userId={}", message, queueName, priority,
                userIdToFilterMessagesByUser);

        if (userIdToFilterMessagesByUser == 0) {
            currentJmsTemplate.convertAndSend(queueName, message);
        }
        else {
            currentJmsTemplate.convertAndSend(queueName, message, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws JMSException {
                    message.setIntProperty("userId", userIdToFilterMessagesByUser);
                    return message;
                }
            });
        }
    }
}
