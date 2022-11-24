package br.com.uan.artemis.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.uan.artemis.QueueService;

@Configuration
@ConditionalOnMissingBean(QueueService.class)
@ConfigurationProperties(prefix = "artemis")
public class ArtemisAutoConfiguration
{
    /*
     * Sobrescrevendo o ConnectionFactory para suportar o consumer-window-size,
     * que é a quantidade máxima de mensagens que um consumidor recebe de uma
     * vez do Artemis.
     */

    @Value("${spring.artemis.broker-url}")
    private String  brokerUrl;

    @Value("${spring.artemis.user}")
    private String  user;

    @Value("${spring.artemis.password}")
    private String  password;

    @Value("${artemis.consumer-window-size}")
    private Integer consumerWindowSize;

    @Bean("connectionFactory")
    public ConnectionFactory connectionFactory() {

        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);

        cf.setUser(user);
        cf.setPassword(password);
        cf.setConsumerWindowSize(consumerWindowSize);

        return cf;
    }

    /*
     * Criando o serviço padrão de filas do Artemis da uan.
     */

    @Autowired
    private JmsTemplate  jmsTemplate;

    @Autowired(required = false)
    private JmsTemplate  secondaryJmsTemplate;

    @Autowired(required = false)
    private ObjectMapper objectMapperForBroker;

    @Bean("queueService")
    public QueueService queueService() {
        return new QueueService(jmsTemplate, secondaryJmsTemplate, objectMapperForBroker);
    }
}
