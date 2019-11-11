# Source Code for CHIME

The Quick Instruction for Running demo.

The demo is in jar folder. The executable jar program is CHIME_release.jar.

The Demo Instruction is Instruction.ppt

Software is Tested in MAC OS X. The Screenshots is in instruction.ppt

Three different type of input parameter can be used for CHIME

For running CHIME with default parameter (l=300, paa=5, a=6)

java -Xmx12g -jar CHIME_release.jar [Time Series File] > [Output File]

For running CHIME with user's motif length: 

java -Xmx12g -jar CHIME_release.jar [Time Series File] [Minimum Motif Length] > [Output File]

For running CHIME with user's motif length and parameters: 

java -Xmx12g -jar CHIME_release.jar [Time Series File] [Minimum Motif Length] [PAA Size] > [Output File]

java -Xmx12g -jar CHIME_release.jar [Time Series File] [Minimum Motif Length] [PAA Size] [Alphabet Size] > [Output File]


For running CHIME with different motif threshold

java -Xmx12g -jar CHIME_release.jar [Time Series File] [PAA Size] [Minimum Motif Length][Alphabet Size][Motif Threshold]> [Output File]


Note: Program generated motif candidat uses “getmoitf.m” to rank. 

## Reference

If you found the code is useful, please cite the paper

```
  @INPROCEEDINGS{chime2018, 
                 author={Y. Gao and J. Lin}, 
                 booktitle={2019 IEEE International Conference on Data Mining (ICDM)}, 
                 title={Discovering Subdimensional Motifs of Different Lengths in Large-Scale Multivariate Time Series}, 
                 year={2019}, 
                 month={Nov}
                 }
```

Dataset References can be found in the paper.


