
# SHACL Play!
SHACL validator and documentation generator **live at https://shacl-play.sparna.fr**

## Cool things you can do with SHACL

With SHACL Play you can:
- validate an RDF dataset against a SHACL specification, and produce a human-readable report ([`validate` command](https://github.com/sparna-git/shacl-play/wiki/Run-SHACL-Play-App-from-command-line#validate))
- generate an HTML documentation of your SHACL application profile ([`doc` command](https://github.com/sparna-git/shacl-play/wiki/Run-SHACL-Play-App-from-command-line#doc)), or a diagram ([`draw` command](https://github.com/sparna-git/shacl-play/wiki/Run-SHACL-Play-App-from-command-line#draw))
- generate a SHACL from an RDF dataset ([`generate` command](https://github.com/sparna-git/shacl-play/wiki/Run-SHACL-Play-App-from-command-line#generate))
- obtain the statistics of an RDF dataset from a SHACL specification ([`analyze` command](https://github.com/sparna-git/shacl-play/wiki/Run-SHACL-Play-App-from-command-line#analyze))
- generate SPARQL queries from a SHACL spec ([`shacl2sparql` command](https://github.com/sparna-git/shacl-play/wiki/Run-SHACL-Play-App-from-command-line#shacl2sparql))

and other cool things with SHACL Rules, too, like applying them for inference.

## Check out Sparnatural, too

If you use SHACL you may be interested to know that the [Sparnatural Knowledge Graph explorer](https://sparnatural.eu) uses a SHACL file for its [configuration](https://docs.sparnatural.eu/how-to-configure-shacl/How-to-configure-Sparnatural-shacl.html). Which means you can use the same SHACL file to document your dataset, validate data, and provide a query UI.

## Running the application.

### As a command-line

The easiest and recommended way to run the application is by using its command-line version. There is no need to locally install the online version.
For this:
- Install Java 11
- [Download the command-line app from the release section](https://github.com/sparna-git/shacl-play/releases/latest)
- Refer to the [command-line documentation](https://github.com/sparna-git/shacl-play/wiki/Run-SHACL-Play-App-from-command-line)

### With Docker
The following `docker` commands should be sufficient to get you started.
First, build the image with:
```
docker build -t shacl-play:latest ./
```
Then, start the application on port `8080` with:
```
docker run -p 8080:8080 shacl-play:latest
```
Refer to [docker documentation](https://docs.docker.com) for advanced configuration.

## Installing locally

Clone this respository:

```bash
git clone git@github.com:sparna-git/shacl-play.git
```

### OSX

Install maven and tomcat (it must be the 9th version):

```bash
brew install maven tomcat@9
```

Go to the directory where this repository was cloned and build the project as a `.war` file:

```bash
mvn package
```

Copy the generated package into tomcat's webapp directory:

```bash
cp shacl-play/target/shacl-play-0.8.0.war /opt/homebrew/Cellar/tomcat@9/9.0.89/libexec/webapps
```

Start the tomcat server:

```bash
 /opt/homebrew/Cellar/tomcat@9/9.0.89/bin/catalina start
```

Go to the default address `http://localhost:8080/shacl-play-0.8.0/` to access SHACL.

Don't forget to stop the server once you don't need it anymore!

```bash
/opt/homebrew/Cellar/tomcat@9/9.0.89/bin/catalina stop
```

### Linux

Install Maven and Tomcat (version 9) using your package manager, here's apt as an example:

```bash
sudo apt-get update
sudo apt-get install maven tomcat9
```

Navigate to the directory where the repository was cloned and build the project as a `.war` file:

```bash
mvn package
```

Copy the generated .war file into Tomcat's webapps directory:

```bash
sudo cp target/shacl-play-0.8.0.war /var/lib/tomcat9/webapps
```


Start the Tomcat server to deploy the application:

```bash
sudo systemctl start tomcat9
```

Access the SHACL-Play web application by navigating to `http://localhost:8080/shacl-play-0.8.0/`

Remember to stop the Tomcat server when it's no longer needed:

```bash
sudo systemctl stop tomcat9
```

#### Troubleshooting

If you're running into issues try to give read and write access to tomcat's SHACL directory, on Mac:

```bash
chmod 644 /opt/homebrew/Cellar/tomcat/10.1.24/libexec/webapps/shacl-play-0.8.0.war
```

Or Linux:

```bash
sudo chmod 644 /var/lib/tomcat9/webapps/shacl-play-0.8.0.war
```

Or stop and re-start the server.

You can also export tomcat's binary path to your `.rc` file and add a nickname to it, like tomcat.
