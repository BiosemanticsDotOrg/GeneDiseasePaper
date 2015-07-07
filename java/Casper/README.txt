##########################################################################
# Casper                                 Version 1.0                     #
# Copyright 2008                         Medical Informatics, Erasmus MC #   
# Author Kristina Hettne                 k.hettne@erasmusmc.nl           #
# Created 14/05/08                       Last Modified 07/01/09          #
# Biosemantics group:                    http://www.biosemantics.com     #
##########################################################################
# Please report any bugs to Kristina Hettne at k.hettne@erasmusmc.nl     #
##########################################################################

Casper - CleAn, SuPprEss, and Rewrite UMLS

IMPORTANT: Casper CAN NOT BE RUN FROM THE CODEBASE ANYMORE!
The Casper/src FOLDER only contains the production code for Casper for reference.

Casper can be downloaded from:
http://www.biosemantics.org/uploads/file/Casper.zip

Casper runs from the command line in Windows or Linux. As default, 
the casper.ini file is read from the current directory but the full 
path to the casper.ini file can also be supplied as an argument if 
the file is placed in another location by the user:
 
java -Xmx1500m -jar casper.jar <optional: location of ini file>

Casper needs the files MRCONSO.RRF and MRSTY.RRF, which can be found in the 
META catalogue in the UMLS version tree. These are by default read from the 
current directory, but their location can be changed by editing the 
variables MRCONSO and MRSTY in the casper.ini file, supplying the new full 
path to the files.

The output from the program is a new MRCONSO file named 
MRCONSO_casper.RRF. Rewritten terms can be recognized 
by the SUI number, which is the 6th field in the MRCONSO file. If a term 
is rewritten, the new term gets the original terms SUI number plus a "+"-sign 
followed by an abbreviation of the name of the rule applied. 
The abbreviations used are the following:

Syntactic inversion = SYN
Possessives = POS
Short Form And Long Form = SFLF
Angular Brackets = ANG
Semantic Type = SEM
Begin Parenthesis = BPA
End Parenthesis = EPA
Begin Brackets = BBR
End Brackets = EBR

A log file is created in which all suppressed terms can be
found. Terms are suppressed if they contain more than 255
charachters, are in a language other than English, have been marked as
suppressible by the UMLS annotation staff, or satisfies the conditions
for the suppress rules.

Which rewrite and suppress rules to apply and other settings can be adjusted in the
casperSettings.ini file. The default settings are the following:

Syntactic inversion = on
Possessives = on
Short Form And Long Form = on
Angular Brackets = on
Semantic Type = on
Begin Parenthesis = off
End Parenthesis = off
Begin Brackets = off
End Brackets = off
Dosages = on
At-sign = on
Short token = on
Any classification = on
Any underspecification = on
EC numbers = on
Miscellaneous = on
More than five words in term = off

These settings correspond with the recommendation from the original
article about the rules.

If the user only wants to use the rewrite rules and not the suppress
rules (or vice versa), he can set the parameters Apply suppressrules
and Apply rewriterules on or off. The default setting is on.