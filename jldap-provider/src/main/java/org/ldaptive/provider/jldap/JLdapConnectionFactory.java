/* See LICENSE for licensing and NOTICE for copyright. */
package org.ldaptive.provider.jldap;

import java.time.Duration;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPException;
import org.ldaptive.ConnectionStrategy;

/**
 * Creates ldap connections using the JLDAP LDAPConnection class.
 *
 * @author  Middleware Services
 */
public class JLdapConnectionFactory extends AbstractJLdapConnectionFactory<JLdapConnection>
{


  /**
   * Creates a new jldap connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  strategy  connection strategy
   * @param  config  provider configuration
   * @param  constraints  connection constraints
   * @param  timeOut  time in milliseconds that operations will wait
   */
  public JLdapConnectionFactory(
    final String url,
    final ConnectionStrategy strategy,
    final JLdapProviderConfig config,
    final LDAPConstraints constraints,
    final Duration timeOut)
  {
    super(url, strategy, config, constraints, timeOut);
  }


  @Override
  protected LDAPConnection createLDAPConnection()
    throws LDAPException
  {
    return new LDAPConnection();
  }


  @Override
  protected JLdapConnection createJLdapConnection(final LDAPConnection conn, final JLdapProviderConfig config)
  {
    return new JLdapConnection(conn, config);
  }
}
