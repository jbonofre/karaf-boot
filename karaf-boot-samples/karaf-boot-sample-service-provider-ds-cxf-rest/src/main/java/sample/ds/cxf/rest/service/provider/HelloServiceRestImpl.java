package sample.ds.cxf.rest.service.provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import sample.ds.cxf.rest.service.provider.api.HelloService;

@Component(service = HelloServiceRest.class, property={"service.exported.interfaces=*", "service.exported.configs=org.apache.cxf.rs", "org.apache.cxf.rs.address=/"})
public class HelloServiceRestImpl implements HelloServiceRest{

    private HelloService helloService;

    public String hello(String message) {
        return helloService.sayHello(message);
    }

    @Reference
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}
