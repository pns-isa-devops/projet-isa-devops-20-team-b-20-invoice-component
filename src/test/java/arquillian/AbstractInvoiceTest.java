package arquillian;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import fr.polytech.invoice.components.DeliveryBilling;
import fr.polytech.invoice.components.InvoiceManager;


public class AbstractInvoiceTest {
    @Deployment
    public static WebArchive createDeployment() {
        // @formatter:off
        return ShrinkWrap.create(WebArchive.class)
                // Components and Interfaces
                .addPackage(DeliveryBilling.class.getPackage())
                .addPackage(InvoiceManager.class.getPackage())
                .addAsLibraries(Maven.resolver()
                            .loadPomFromFile("pom.xml")
                            .importRuntimeDependencies()
                            .resolve()
                            .withTransitivity()
                            .asFile());
        // @formatter:on
    }
}