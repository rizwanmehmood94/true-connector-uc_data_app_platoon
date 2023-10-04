package policies;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConnectorRestrictAccessTest extends AbstracPolicyTest {
	
	@Test
	public void createConnectorRestrictAccessTest() throws IOException {
		ContractAgreement ca = createAgreement();

		assertNotNull(ca);
		System.out.println(serializer.serialize(ca));
	}

	private ContractAgreement createAgreement() {
		Constraint c = new ConstraintBuilder()
				._leftOperand_(LeftOperand.SYSTEM)
				._operator_(BinaryOperator.SAME_AS)
				._rightOperand_(new RdfResource("https://example.com"))
				.build();
		return createContractAgreement(Util.asList(c));
		
	}
}
