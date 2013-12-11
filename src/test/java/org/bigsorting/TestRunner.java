package org.bigsorting;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Hanna Eismant
 *         11.12.13
 */
public class TestRunner extends Assert{

    @Test
    public void testRunProgram() {
         Runner.main(null);
    }

}
