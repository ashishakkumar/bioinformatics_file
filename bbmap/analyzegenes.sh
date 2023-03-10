#!/bin/bash

usage(){
echo "
Written by Brian Bushnell
Last modified September 27, 2018

Description:  Generates a prokaryotic gene model (.pkm) for gene calling.
Input is fasta and gff files.
The .pkm file may be used by CallGenes.

Usage:  analyzegenes.sh in=x.fa gff=x.gff out=x.pgm

File parameters:
in=<file>       A fasta file or comma-delimited list of fasta files.
gff=<file>      A gff file or comma-delimited list.  This is optional;
                if present, it must match the number of fasta files.
                If absent, a fasta file 'foo.fasta' will imply the
                presence of 'foo.gff'.
out=<file>      Output pgm file.

Please contact Brian Bushnell at bbushnell@lbl.gov if you encounter any problems.
"
}

#This block allows symlinked shellscripts to correctly set classpath.
pushd . > /dev/null
DIR="${BASH_SOURCE[0]}"
while [ -h "$DIR" ]; do
  cd "$(dirname "$DIR")"
  DIR="$(readlink "$(basename "$DIR")")"
done
cd "$(dirname "$DIR")"
DIR="$(pwd)/"
popd > /dev/null

#DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/"
CP="$DIR""current/"

z="-Xmx1g"
z2="-Xms1g"
EA="-ea"
EOOM=""
set=0

if [ -z "$1" ] || [[ $1 == -h ]] || [[ $1 == --help ]]; then
	usage
	exit
fi

calcXmx () {
	source "$DIR""/calcmem.sh"
	parseXmx "$@"
}
calcXmx "$@"

function analyze() {
	if [[ $SHIFTER_RUNTIME == 1 ]]; then
		#Ignore NERSC_HOST
		shifter=1
	elif [[ $NERSC_HOST == genepool ]]; then
		module unload oracle-jdk
		module load oracle-jdk/1.8_144_64bit
		module load pigz
	elif [[ $NERSC_HOST == denovo ]]; then
		module unload java
		module load java/1.8.0_144
		module load pigz
	elif [[ $NERSC_HOST == cori ]]; then
		module use /global/common/software/m342/nersc-builds/denovo/Modules/jgi
		module use /global/common/software/m342/nersc-builds/denovo/Modules/usg
		module unload java
		module load java/1.8.0_144
		module load pigz
	fi
	local CMD="java $EA $EOOM $z $z2 -cp $CP prok.AnalyzeGenes $@"
	#echo $CMD >&2
	eval $CMD
}

analyze "$@"
