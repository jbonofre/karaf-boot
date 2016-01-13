package sample.blueprint.cxf.rest.service.provider;

import sample.blueprint.cxf.service.provider.api.HelloService;

public class HelloServiceRestImpl implements HelloServiceRest{

    private HelloService helloService;

    public String hello(String message) {
        return helloService.sayHello(message);
    }

    public HelloService getHelloService() {
        return helloService;
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }

}
