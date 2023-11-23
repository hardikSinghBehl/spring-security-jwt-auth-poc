## API Access Control
Every JWT generated by the system corresponding to an authenticated user contains the scope of permission(s) the user is allowed i.e they define the APIs, the generated JWT can access. This is embeded into the JWT in the `scp` claim and allows the process of access-control/authorization to be stateless as well. Below attached is sample JWT payload.

```
{
  "scp": "userprofile.read userprofile.update useridentity.verify",
  "jti": "bd725e30-6d3f-45b4-aed6-625273f63dc0",
  "iss": "cerberus",
  "iat": 1698565369,
  "exp": 1698567169,
  "aud": "7e7765db-4ad2-4ad2-926f-a4060f7fb847"
}
```
### Scopes Assignment in JWT based on User Status

When a JWT is generated for a user, the scopes assigned within the token are determined by the user's current status within the system. 

* The default status of `PENDING_APPROVAL` is assigned when a [User](https://github.com/hardikSinghBehl/jwt-auth-flow-spring-security/blob/master/src/main/java/com/behl/cerberus/entity/User.java) record is created.
* It gets changed to `APPROVED` once the user verifies their identity, i.e once the `/users/identity-verification` API is invoked. In the context of the POC, no actual identity verification is performed by the [IdentityVerificationService.](https://github.com/hardikSinghBehl/jwt-auth-flow-spring-security/blob/master/src/main/java/com/behl/cerberus/service/IdentityVerificationService.java)
* An authenticated user has the capability to deactivate thier account, in doing so the user status gets updated to `DEACTIVATED`.

<div align="center">
  
| User Status      |  Assigned Scopes                                    | 
|------------------|--------------------------------------------|
| **PENDING_APPROVAL** (Default) | userprofile.read, userprofile.update, useridentity.verify |
| **APPROVED**         | fullaccess                                 |
| **DEACTIVATED**     | userprofile.read                           |

</div>

### Enforcing Access Control on APIs

Access Control based on scopes is imposed on each secured API in the application. The below secure API will only be accessible with JWT having scope `userprofile.read` or `fullaccess`.
If a JWT is received by the application having Insufficient Scopes, then a 403 Forbidden error will be thrown. Below attached is a sample code snippet on how scope validation is imposed on APIs. The scopes are converted to List of authorities which are stored in the security context by [JwtAuthenticationFilter.](https://github.com/hardikSinghBehl/jwt-auth-flow-spring-security/blob/master/src/main/java/com/behl/cerberus/filter/JwtAuthenticationFilter.java)

<center>
<img alt="api-access-control" src="https://github.com/hardikSinghBehl/jwt-auth-flow-spring-security/assets/69693621/6a1bdf4b-3238-4240-9f3a-48475d118600">
</center>

### Handling User Status Changes with Refresh Tokens
In a scenario where a logged-in user's status is updated by the system, the existing JWT held by the Web Application will retain the previous scopes and won’t grant access to the new APIs that the user logically should be able to invoke now. If the user's permissions have changed, the client can leverage available refresh token to request a new JWT, reflecting the new permissions that the user has obtained. This process can be configured to execute in an event when `403 Forbidden` is encountered while invoking a Backend API

---

### Key Components

* [UserStatus.java](https://github.com/hardikSinghBehl/jwt-auth-flow-spring-security/blob/master/src/main/java/com/behl/cerberus/entity/UserStatus.java)
* [JwtUtility.java](https://github.com/hardikSinghBehl/jwt-auth-flow-spring-security/blob/master/src/main/java/com/behl/cerberus/utility/JwtUtility.java)
* [JwtAuthenticationFilter.java](https://github.com/hardikSinghBehl/jwt-auth-flow-spring-security/blob/master/src/main/java/com/behl/cerberus/filter/JwtAuthenticationFilter.java)