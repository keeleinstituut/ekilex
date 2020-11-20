package eki.ekilex.manual;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.ekilex.service.LoaderConfService;

public abstract class AbstractLoader implements LoaderConstant, GlobalConstant {

	abstract void execute(String[] args);

	private ConfigurableApplicationContext applicationContext;

	protected LoaderConfService confService;

	protected void initDefault() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		applicationContext.registerShutdownHook();
		confService = applicationContext.getBean(LoaderConfService.class);
	}

	protected void initWithTermeki() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml", "db-termeki-config.xml");
		applicationContext.registerShutdownHook();
		confService = applicationContext.getBean(LoaderConfService.class);
	}

	protected <T> T getComponent(Class<T> componentType) {
		return applicationContext.getBean(componentType);
	}

	protected void shutdown() {
		applicationContext.close();
	}

}
