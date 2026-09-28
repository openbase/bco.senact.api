# bco.senact.api
Network control API for the homeautomation gateway module senact.

## Publishing

Pushing to `dev` publishes the configured `-SNAPSHOT` version to Maven Central's
snapshot repository. Creating a GitHub release publishes the version from its tag
(for example, `v3.0.0` publishes `3.0.0`) to Maven Central.

Configure these GitHub Actions repository secrets before enabling publishing:

- `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_TOKEN`: Sonatype Central credentials.
- `OPENBASE_GPG_PRIVATE_KEY`: Base64-encoded armored private signing key.
- `OPENBASE_GPG_PRIVATE_KEY_PASSPHRASE`: signing-key passphrase (may be empty).

For local publication, provide the same values as Gradle project properties and run
`./gradlew publishToSonatype`.
