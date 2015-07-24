# dropwizard-version-bundle

A [Dropwizard][dropwizard] bundle that exposes the version of your application as well as its
dependencies via the admin port.

[![Build Status](https://secure.travis-ci.org/dropwizard-bundles/dropwizard-version-bundle.png?branch=master)]
(http://travis-ci.org/dropwizard-bundles/dropwizard-version-bundle)


## Getting Started

Just add this maven dependency to get started:

In your pom.xml define `<dropwizard.version.bundle>0.x</dropwizard-version-bundle.version>`

Make sure to keep `dropwizard-version-bundle.version in sync` with the major dropwizard version (0.6, 0.7, 0.8)

Dropwizad 0.6 aplication example :

```xml

<dropwizard-version-bundle.version>0.6</dropwizard-version-bundle.version>

...

<dependency>
  <groupId>fr.novapost.dropwizard-bundles</groupId>
  <artifactId>dropwizard-version-bundle</artifactId>
  <version>${dropwizard-version-bundle.version}</version>
</dependency>
```


Add the bundle to your environment using your choice of version supplier:

```java
public class MyApplication extends Application<Configuration> {
  @Override
  public void initialize(Bootstrap<Configuration> bootstrap) {
    VersionSupplier supplier = new MavenVersionSupplier("<YOUR GROUP>", "<YOUR ARTIFACT ID>");
    bootstrap.addBundle(new VersionBundle(supplier));
  }

  @Override
  public void run(Configuration cfg, Environment env) throws Exception {
    // ...
  }
}
```

Now you can access the the `/version` URL of your application to see the version
of your application as well as its dependencies.

For example if your application were running on `localhost` with the admin server on port 8081 then
something like the following would show you your application's version.

```bash
curl localhost:8080/version
```

## Customizing the version supplier

By default the bundle only comes with a single version supplier `MavenVersionSupplier` that will
discover the version information for a particular maven artifact by reading the `pom.properties`
file in a maven produced jar.

The `VersionSupplier` that plugs into the bundle is customizable however so you can define your own
implementation if your needs are different.  If you implement a new `VersionSupplier` that is
generally useful for others then please feel free to submit a pull request.


[dropwizard]: http://dropwizard.io
