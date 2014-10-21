Things not in demo

1. use applicactionContextxml

	@ImportResource("applicationContext.xml")
	public class BootServiceApplication {
	}


2. use Jetty instead of Tomcat

3. disable  web in junit test use spring context

	setWebEnvironment(false)

4. set system exit code

	org.springframework.boot.ExitCodeGenerator

5. read configuration
	@Value(${name})
	String name

    or
    
    @Autowired  
    Environment env;
    env.get  
    
6. set the configuration file path

   java -jar myproject.jar --spring.config.location=classpath:/default.properties,classpath:/override.properties
   
   or just change the file name
   
   java -jar myproject.jar --spring.config.name=myproject
   
7. active profile
   java -jar myproject.jar --spring.profiles.active=dev,hsqldb
   
   SpringApplication.setAdditionalProfiles(…​)
   
8. define logging level in appliation.properties

logging.level.org.springframework.web: DEBUG
logging.level.org.hibernate: ERROR

9. add spring-mvc defenation
wtrite WebMvcConfigurerAdapter without @EnableWebMvc

10. add servlet/filter

in application.properties

ServletRegistrationBean or FilterRegistrationBean 

11. configure web

add context path
server.contextPath

set tomcat threads

12. JMX

@ManagedResource, @ManagedAttribute, @ManagedOperation
