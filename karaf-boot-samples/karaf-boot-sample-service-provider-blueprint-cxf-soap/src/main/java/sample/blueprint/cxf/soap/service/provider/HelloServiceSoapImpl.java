package sample.blueprint.cxf.soap.service.provider;

import sample.blueprint.cxf.service.provider.api.HelloService;

import javax.jws.WebService;

@WebService(endpointInterface = "sample.blueprint.cxf.soap.service.provider.HelloServiceSoap",
        serviceName="HelloKaraf")
public class HelloServiceSoapImpl implements HelloServiceSoap {

    private HelloService helloService;

    public String Hello(String name) {
        return helloService.sayHello(name);
    }

    public HelloService getHelloService() {
        return helloService;
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}
