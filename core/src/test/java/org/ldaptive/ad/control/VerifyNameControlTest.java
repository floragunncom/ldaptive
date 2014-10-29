/*
  $Id: VerifyNameControlTest.java 3005 2014-07-02 14:20:47Z dfisher $

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3005 $
  Updated: $Date: 2014-07-02 10:20:47 -0400 (Wed, 02 Jul 2014) $
*/
package org.ldaptive.ad.control;

import org.ldaptive.LdapUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link VerifyNameControl}.
 *
 * @author  Middleware Services
 * @version  $Revision: 3005 $ $Date: 2014-07-02 10:20:47 -0400 (Wed, 02 Jul 2014) $
 */
public class VerifyNameControlTest
{


  /**
   * Verify name control test data.
   *
   * @return  response test data
   */
  @DataProvider(name = "request")
  public Object[][] createData()
  {
    return
      new Object[][] {
        // BER:
        // 30:14:02:01:00:04:0F:61:64:2E:6C:64:61:70:74:69:76:65:2E:6F:72:67
        new Object[] {
          LdapUtils.base64Decode("MBQCAQAED2FkLmxkYXB0aXZlLm9yZw=="),
          new VerifyNameControl("ad.ldaptive.org"),
        },
      };
  }


  /**
   * @param  berValue  to encode.
   * @param  expected  verify name control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"control"},
    dataProvider = "request"
  )
  public void encode(final byte[] berValue, final VerifyNameControl expected)
    throws Exception
  {
    Assert.assertEquals(expected.encode(), berValue);
  }
}