Cloudhopper, Inc.
http://www.cloudhopper.com/
Commons Remote FileSystem Library
--------------------------------------------------------------------------------

Cloudhopper Commons RFS supports copying and moving files to remote filesystems
Various protocols are supported such as FTP, SSL/TLS FTP, Secure FTP, and
possibly more in the future.  Each is configured with a simple URL-based syntax.
Authentication is supported for each protocol.

SFTP
--------------------------------------------------------------------------------
Provides access to the files on an SFTP server (that is, an SSH or SCP server).

URI Format

  sftp://username[: password ]@hostname[: port ][ path ]

Examples

  sftp://myusername:mypassword@somehost
  sftp://myusername:mypassword@somehost/
  sftp://myusername:mypassword@somehost/pub/downloads/

The default port is 22.  At a minimum, a "user" and "host" MUST be provided in
the URI. The "path" component determines which directory the
files will be put.  If no trailing slash is present after the host or port, then
after logging in, the library will stay in the default directory on the remote
system.  Most times, this is usually the user's home directory.  However, if a
"path" is provided, then this will be treated as an absolute path since it must
start with a leading slash /.  Meaning, if by default you are logged into
/home/user and your URI is this "sftp://myusername:mypassword@somehost/", then
you'll end up in the root directory /.  This is assuming the SFTP server allows
access to root and doesn't have some sort of "root jail" in place.

Also, any host will be trusted by default during a connection.

Password-less authentication is supported for private keys that aren't protected
by a passphrase.  By default, the library will *always* attempt to load any
private key it can find in the user running the application.  For example, if
user "usera" is running the Java program, then the application will attempt to
find the ".ssh" directory in that user's home directory.  Inside that directory,
it will look for an "id_dsa" or "id_rsa" private key file.  It will then attempt
to load them.

FTP and FTPS
--------------------------------------------------------------------------------
Provides access to the files on an FTP or FTPS server (SSL-protected).

URI Format

  ftp[s]://username[: password ]@hostname[: port ][ path ]

Examples

  ftp://myusername:mypassword@somehost
  ftp://myusername:mypassword@somehost/
  ftp://myusername:mypassword@somehost/pub/downloads/
  ftps://myusername:mypassword@somehost
  ftps://myusername:mypassword@somehost/
  ftps://myusername:mypassword@somehost/pub/downloads/

The default port is 21.  SSL/TLS support usually still connects via port 21 and
then requests an SSL-protected session.  Only a hostname is required in the FTP
or FTPS URIs.  If no username is provided, then this library will attempt to
login anonymously with an empty password.  If an anonymous login requires a
password to be sent, then you'll need to manually include those in the URI such
that "ftp://anonymous:pass@host".  Also, after connecting, this library will
immediately switch to both passive and binary modes.
