# DicomReader
[![License: GPLv3][GPLimg]][GPLurl]
[![GitHub Release][GHRimg]][GHRurl]

Copyright (C) 2005 Salvatore La Bua [slabua(at)gmail.com](mailto:slabua@gmail.com)  
http://www.slblabs.com/projects/dicomreader  
http://github.com/slabua/dicomreader  

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
## Table of Contents

- [Introduction to the Project](#introduction-to-the-project)
- [Resources](#resources)
- [LICENSE](#license)
- [Screenshots](#screenshots)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->
## Introduction to the Project

- DicomReader is a simple Java DICOM files decipher and it has been developed
  as part of the [Volumetric Bias Correction][R00] paperwork.
- DicomReader handles headers as well as images contained in the Dicom files:
  - Data (headers and pixel-value images) are saved into ascii plain text files.
  - A pgm version of the image files is provided as an option for the user.
- The graphical user interface, based on Swing components, allows the user to
  choose the file names where headers and images will be saved to.  
  If a pgm image is also desired, its name is automatically generated from the
  ascii text image’s file name.
- DicomReader needs a Dicom dictionary in order for it to work, where it can
  read Dicom tags from and take the correct action for each of them,
  accordingly.
- If multi-sliced Dicom files are provided, results will be saved with the
  same file basename selected in the user interface and a sequence number will
  be appended at its end, keeping the original file name extension unchanged
  (if provided by the user).

## Resources

1. [ResearchGate conference paper publication][R01]
2. [ResearchGate related project][R02]

## LICENSE

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

## Screenshots

![main-1.3.3][S01]

![info-1.3.3][S02]

![exit-1.3.3][S03]

[GPLimg]: https://img.shields.io/badge/License-GPLv3-blue.svg
[GPLurl]: https://www.gnu.org/licenses/gpl-3.0
[GHRimg]: https://img.shields.io/github/release/slabua/dicomreader.svg
[GHRurl]: https://github.com/slabua/dicomreader/releases
[R00]: http://dx.doi.org/10.1007/978-3-540-71457-6_48
[R01]: https://goo.gl/ZL4QGx
[R02]: https://goo.gl/um4tbP
[S01]: https://goo.gl/TGu9E7
[S02]: https://goo.gl/zPc3sG
[S03]: https://goo.gl/hAUW6X

