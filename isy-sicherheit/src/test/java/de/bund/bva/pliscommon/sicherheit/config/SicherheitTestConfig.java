package de.bund.bva.pliscommon.sicherheit.config;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import de.bund.bva.pliscommon.sicherheit.accessmgr.test.TestAccessManager;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.Service2Impl;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.ServiceImpl;
import de.bund.bva.pliscommon.sicherheit.impl.SicherheitImpl;
import de.bund.bva.pliscommon.sicherheit.web.DelegatingAccessDecisionManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class SicherheitTestConfig {

    @Bean
    public Sicherheit sicherheit(AufrufKontextVerwalter aufrufKontextVerwalter, AufrufKontextFactory aufrufKontextFactory, AccessManager accessManager, IsySicherheitConfigurationProperties properties) {
        SicherheitImpl sicherheit = new SicherheitImpl("/resources/sicherheit/rollenrechte.xml", aufrufKontextVerwalter, aufrufKontextFactory, accessManager, properties);

        return sicherheit;
    }

    @Bean
    public TestAccessManager accessManager() {
        return new TestAccessManager();
    }

    @Bean
    public DelegatingAccessDecisionManager accessDecisionManager(Sicherheit sicherheit) {
        return new DelegatingAccessDecisionManager(sicherheit);
    }

    @Bean
    public AufrufKontextVerwalterStub aufrufKontextVerwalter(AufrufKontextFactory aufrufKontextFactory) {
        AufrufKontextVerwalterStub aufrufKontextVerwalter = new AufrufKontextVerwalterStub();
        aufrufKontextVerwalter.setAufrufKontextFactory(aufrufKontextFactory);
        return aufrufKontextVerwalter;
    }

    @Bean
    public ServiceImpl service() {
        return new ServiceImpl();
    }

    @Bean
    public Service2Impl service2() {
        return new Service2Impl();
    }
}
