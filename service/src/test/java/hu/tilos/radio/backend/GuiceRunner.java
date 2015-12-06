package hu.tilos.radio.backend;

import com.github.fakemongo.junit.FongoRule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.mongodb.DB;
import hu.tilos.radio.backend.spark.GuiceConfigurationListener;
import org.dozer.DozerBeanMapper;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.validation.Validator;

public class GuiceRunner implements TestRule {

    public GuiceRunner(Object obj) {

        FongoCreator creator = new FongoCreator();
        creator.createDB();
        creator.init();
        Injector i = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(FongoRule.class).toInstance(creator.createRule());
                bind(DB.class).toInstance(creator.createDB());
                bind(DozerBeanMapper.class).toProvider(DozerFactory.class).asEagerSingleton();
                bind(Validator.class).toProvider(hu.tilos.radio.backend.ValidatorProducer.class);
                bindListener(Matchers.any(), new GuiceConfigurationListener());
            }


        });

        i.injectMembers(obj);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return base;
    }

}
