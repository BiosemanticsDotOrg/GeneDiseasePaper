# Concept profile generation and analysis for Gene-Disease paper
# Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
#  Leiden, The Netherlands
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>

__author__ = 'rajaram'

import re

line = '3063788,1486,0.0014699738497795524,"[19344727, 22207712]"'

line2 = '3063788,1486,0.0014699738497795524,[]'

matchscore = float(line.split(",")[2])
cooccurance = line.split(",")[3]
cooccurance = re.sub("[^0-9]", "", cooccurance)

print(len(cooccurance))

cooccurance = line2.split(",")[3]
cooccurance = re.sub("[^0-9]", "", cooccurance)
print(len(cooccurance))
