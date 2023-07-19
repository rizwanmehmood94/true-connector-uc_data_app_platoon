package policies;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SecurityLevelRestrictAccessTest extends AbstracPolicyTest {
	
	@Test
	public void createSecurityLevelRestrictAccessTest() throws IOException {
		ContractAgreement ca = createAgreement();

		assertNotNull(ca);
		System.out.println(serializer.serialize(ca));
	}

	private ContractAgreement createAgreement() {
		Constraint c = new ConstraintBuilder()
				._leftOperand_(LeftOperand.SECURITY_LEVEL)
				._operator_(BinaryOperator.EQ)
				._rightOperand_(new RdfResource(SecurityProfile.BASE_SECURITY_PROFILE.toString()))
				.build();
		return createContractAgreement(Util.asList(c));
		
	}
}
