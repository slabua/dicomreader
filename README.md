# DicomReader
[![License: GPLv3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![GitHub Release](https://img.shields.io/github/release/slabua/dicomreader.svg)](https://github.com/slabua/dicomreader/releases)

Copyright : (C) 2005 Salvatore La Bua [slabua(at)gmail.com](mailto:slabua@gmail.com)  
http://www.slblabs.com/projects/dicomreader  
http://github.com/slabua/dicomreader  

---

## Introduction to the Project

- DicomReader is a simple Java DICOM files decipher and it has been developed
  as part of the [Volumetric Bias Correction](http://dx.doi.org/10.1007/978-3-540-71457-6_48) paperwork.
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

1. [ResearchGate conference paper publication](https://goo.gl/ZL4QGx)
2. [ResearchGate related project](https://goo.gl/um4tbP)

---

![main-1 3 2-1](https://f.cloud.github.com/assets/1002978/1172515/d81588e8-2122-11e3-873a-1e23af9af495.png)

![info2-1 3 2-1](https://f.cloud.github.com/assets/1002978/1211563/8853de7a-260b-11e3-86ef-c31fb65621e6.png)

![exit-1 3 2-1](https://f.cloud.github.com/assets/1002978/1172520/f5c08cbc-2122-11e3-8c03-0d3ff314c459.png)

---

