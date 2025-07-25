package bank.rest.app.bankrestapp.entity.enums;

/**
 * Enumeration representing user roles in the banking system.
 *
 * <p>This enum defines the authorization levels and permissions for different
 * types of users in the system. Each role determines what operations and
 * resources a user can access.</p>
 *
 * <p>Role hierarchy and permissions:</p>
 * <ul>
 *   <li>ROLE_ADMIN - Full system access, can manage all users and accounts</li>
 *   <li>ROLE_USER - Standard customer access, can manage own accounts only</li>
 * </ul>
 *
 * <p>These roles are used by Spring Security for authentication and authorization
 * throughout the application.</p>
 *
 * @see bank.rest.app.bankrestapp.entity.CustomerRole
 * @see bank.rest.app.bankrestapp.config.SecurityConfig
 *
 * @author Nazira Savisska
 * @since 1.0
 */
public enum Role {

    /** Administrative role with full system privileges and user management capabilities */
    ROLE_ADMIN,

    /** Standard user role for regular customers with limited access to own resources */
    ROLE_USER
}
