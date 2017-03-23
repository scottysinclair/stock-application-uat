Notes:

EAP7 uses RestEasy as the JAX-RS provider.
docker-java for some reason does not play nicely with RestEasy and should use JerseyRS instead.

The problem is that EAP7 now uses resteasy inside ApacheCXF. ApacheCXF used to use it's own JAX-RS implementation.

I wanted to exclude the resteasy module dependency from the test WAR file which is being deployed, but this will
cause havok with any Apache CXF webservice clients which may be in the test WAR as part of the application under test

