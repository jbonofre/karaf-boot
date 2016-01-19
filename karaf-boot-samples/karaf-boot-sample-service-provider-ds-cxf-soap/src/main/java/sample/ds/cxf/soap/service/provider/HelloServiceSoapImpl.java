package sample.ds.cxf.soap.service.provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import sample.ds.cxf.rest.service.provider.api.HelloService;

import javax.jws.WebService;

@WebService(endpointInterface = "sample.ds.cxf.soap.service.provider.HelloServiceSoap",
        serviceName="HelloKaraf")
@Component(service = HelloServiceSoap.class, property={"service.exported.interfaces=*", "service.exported.configs=org.apache.cxf.ws", "org.apache.cxf.ws.address=/hello"})

public class HelloServiceSoapImpl implements HelloServiceSoap {

    private HelloService helloService;

    public String Hello(String name) {
        return helloService.sayHello(name);
    }

    @Reference
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}
