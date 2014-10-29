/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.props;

/**
 * Handles simple properties common to all objects.
 *
 * @author  Middleware Services
 * @version  $Revision: 2885 $ $Date: 2014-02-05 16:28:49 -0500 (Wed, 05 Feb 2014) $
 */
public class SimplePropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new simple property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public SimplePropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    return convertSimpleType(type, value);
  }
}