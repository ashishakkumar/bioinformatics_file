#!/bin/bash

bbmap/bbduk.sh -Xmx2g in1="D12_17539_ATCTCA_read1_part.fastq"  in2="D12_17539_ATCTCA_read2_part.fastq" out1="Iter1_read1_trimmed.fastq" out2="Iter1_read2_trimmed.fastq" ref="bbmap/resources/adapters.fa" ktrim=r k=23 mink=11 hdist=1 tpe tbo
