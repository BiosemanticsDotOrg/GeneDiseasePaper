Run the ParseAll file to parse medline XML files.
Please adjust config.properties in the util package to fit your DB connection

DB Scheme for MySQL can be found in package Design.


IMPORTANT !!!!

Make sure to update / import in chronologic order.
If you want to redo one single file, you have to parse all the consecutive files aswell in order to process updated records.

Uze GZ compressed files

ftp://ftp.nlm.nih.gov

BASELINE: /nlmdata/.medleasebaseline/gz
UPDATES : /nlmdata/.medlease/gz