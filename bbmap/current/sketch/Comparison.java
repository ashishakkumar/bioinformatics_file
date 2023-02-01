package sketch;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import shared.Tools;

public final class Comparison extends SketchObject implements Comparable<Comparison> {
	
	/*--------------------------------------------------------------*/
	/*----------------         Constructors         ----------------*/
	/*--------------------------------------------------------------*/
	
	public Comparison(){}
	
	public Comparison(CompareBuffer buffer){
		this(buffer, null, null);
	}
	
	public Comparison(Sketch a_, Sketch b_){
		this(null, a_, b_);
	}
	
	public Comparison(CompareBuffer buffer, Sketch a_, Sketch b_){
		
		a=a_;
		b=b_;
		
		if(buffer!=null){setFrom(buffer);}
		
		if(b!=null){
			taxName=b.taxName();
			taxID=b.taxID;
		}

//		System.err.println(this);
//		System.err.println(b.present);
	}
	
	/*--------------------------------------------------------------*/
	/*----------------           Mutators           ----------------*/
	/*--------------------------------------------------------------*/
	
	public void setFrom(CompareBuffer buffer){
		hits=buffer.hits();
		multiHits=buffer.multiHits();
		unique2=buffer.unique2();
		unique3=buffer.unique3();
		noHits=buffer.noHits();

		contamHits=buffer.contamHits();
		contam2Hits=buffer.contam2Hits();
		multiContamHits=buffer.multiContamHits();
		
		refDivisor=buffer.refDivisor();
		queryDivisor=buffer.queryDivisor();
		
		refSize=buffer.refSize();
		querySize=buffer.querySize();

		depth=buffer.depth();
		depth2=buffer.depth2();
		float x=buffer.avgRefHits();
		if(x>0){avgRefHits=x;}
//		volume=volume0();
		score=score0();

		hits1=buffer.hits1();
		qSeen1=buffer.qSeen1();
		rSeen1=buffer.rSeen1();
	}
	
	public void recompare(CompareBuffer buffer, int[][] taxHits, int contamLevel){
		
//		for(int[] row : taxHits){
//			if(row!=null){
//				System.err.println(Arrays.toString(row));
//			}
//		}//Tested; correctly indicates most rows have octopus but some have other things.
		
		assert(a.merged());
//		int oldContam2=contam2Hits;
		int x=a.countMatches(b, buffer, a.compareBitSet(), false, taxHits, contamLevel);
		assert(x==hits);
		setFrom(buffer);
//		contam2Hits=oldContam2;
	}
	
	/*--------------------------------------------------------------*/
	/*----------------            Methods           ----------------*/
	/*--------------------------------------------------------------*/
	
	@Override
	public boolean equals(Object b){
		if(b==null || b.getClass()!=this.getClass()){return false;}
		return scoreComparator.compare(this, (Comparison)b)==0;
	}
	
//	//WKID
//	public float wkid(){return idMinDivisor();}
//	public float idMinDivisor(){
//		return hits/(float)minDivisor();
//	}
	
//	public float k1Fraction(){
//		return a.k1Fraction();
//	}
	
//	//KID
//	public float kid(){return idMaxDivisor();}
//	public float idMaxDivisor(){
//		return hits/(float)maxDivisor();
//	}
	
	public float idQueryDivisor(){
		return hits/(float)(Tools.max(1, refDivisor));
	}
	
	public float idRefDivisor(){
		return hits/(float)(Tools.max(1, refDivisor));
	}
	
	public float completeness(){
		float complt=Tools.min(1, (queryDivisor-contamHits)/(float)Tools.max(1, refDivisor));
		return complt;
//		float c2=hits/(float)refDivisor;
//		assert(queryDivisor-contamHits>=hits);
//		assert(c1>=c2);
//		System.err.println(hits+", "+contamHits+", "+refDivisor+", "+queryDivisor+", "+c1+", "+c2);
//		return Tools.max(c1, c2);
//		float kid=idMaxDivisor(), wkid=idMinDivisor();
//		return kid==0 ? 0 : kid/wkid;
	}
	
	public float contamFraction(){
		return Tools.min(1, contamHits/(float)Tools.max(1, queryDivisor));
	}
	
	public float contam2Fraction(){
		return Tools.min(1, contam2Hits/(float)Tools.max(1, queryDivisor));
	}
	
	public float uContamFraction() {
		int uContamHits=contamHits-multiContamHits;
		return Tools.min(1, uContamHits/(float)Tools.max(1, queryDivisor));
	}
	

	
	/*--------------------------------------------------------------*/
	
	final float wkid(){
		final int div=minDivisor();
		return hits/(float)div;
	}
	final float kid(){
		final int div=maxDivisor();
		return hits/(float)div;
	}
	final float aniOld(){
		float wkid=wkid();
//		final float ani=wkidToAni(wkid, k1Fraction());
		final float ani=wkidToAni(wkid);
		return ani;
	}
	final float ani(){
		if(hits<1){return 0;}
		final float ani;
		if(k2>0 && useToValue2){
			float ani1=ani1();
			float ani2=ani2();
//			ani=0.5f*(ani1+ani2);
			ani=0.5f*(Tools.max(0.9f*ani2, ani1)+Tools.max(0.8f*ani1, ani2));
//			return (ani1*qSeen1+ani2*qSeen2())/queryDivisor;
			
//			System.err.println("ani="+ani+"aniOld="+aniOld()+", ani1="+ani1()+", ani2="+ani2()+", anid="+(float)aniDual()+"\n"
////					+"gf="+(float)gf+", wkid1="+wkid1+", wkid2="+wkid2+"\n"
//							+ "k1f="+k1Fraction()+", hits="+hits+", hits1="+hits1+", hits2="+hits2()+", qSeen1()="+qSeen1()+", rSeen1()="+rSeen1()+"\n"
//									+ "qSeen2()="+qSeen2()+", rSeen2()="+rSeen2()+", minDivisor1()="+minDivisor1()+", minDivisor2()="+minDivisor2()+"\n");
		}else{
			ani=aniOld();
		}
		return ani;
	}

	final float wkid1(){
		final int div=minDivisor1();
		return hits1()/(float)div;
	}
	final float kid1(){
		final int div=maxDivisor1();
		return hits1()/(float)div;
	}
	final float ani1(){
		float wkid=wkid1();
		final float ani=wkidToAniExact(wkid, k);
		return ani;
	}

	final float wkid2(){
		final int div=minDivisor2();
		return hits2()/(float)div;
	}
	final float kid2(){
		final int div=maxDivisor2();
		return hits2()/(float)div;
	}
	final float ani2(){
		assert(k2>0);
		float wkid=wkid2();
		final float ani=wkidToAniExact(wkid, k2);
		return ani;
	}
	
	final float aniDual(){
		assert(k2>0);
		float wkid1=wkid1();
		float wkid2=wkid2();
		float ratio=(wkid1/wkid2);
		float exp=1f/(k-k2);//TODO - make this initialized
		double ani=Math.pow(ratio, exp);
		double gf=wkid2/Math.pow(ani, k2);
		
//		System.err.println("ani="+ani()+"aniOld="+aniOld()+", ani1="+ani1()+", ani2="+ani2()+", anid="+(float)ani+"\n"
//				+"gf="+(float)gf+", wkid1="+wkid1+", wkid2="+wkid2+"\n"
//						+ "k1f="+k1Fraction()+", hits="+hits+", hits1="+hits1+", hits2="+hits2()+", qSeen1()="+qSeen1()+", rSeen1()="+rSeen1()+"\n"
//								+ "qSeen2()="+qSeen2()+", rSeen2()="+rSeen2()+", minDivisor1()="+minDivisor1()+", minDivisor2()="+minDivisor2()+"\n");
		
		return (float)ani;
	}
	
	/*--------------------------------------------------------------*/

	int hits1(){return hits1;}
	int qSeen1(){return qSeen1;}
	int rSeen1(){return rSeen1;}
	int minDivisor1(){return Tools.max(1, Tools.min(qSeen1, rSeen1));}
	int maxDivisor1(){return Tools.max(1, qSeen1, rSeen1);}

	int hits2(){return hits-hits1;}
	int qSeen2(){return queryDivisor-qSeen1;}
	int rSeen2(){return refDivisor-rSeen1;}
	int minDivisor2(){return Tools.max(1, Tools.min(qSeen2(), rSeen2()));}
	int maxDivisor2(){return Tools.max(1, qSeen2(), rSeen2());}
	
	/*--------------------------------------------------------------*/
	
//	public float aniOld(){
//		if(hits<1){return 0;}
//
////		double wkid=aniFromWKID ? idMinDivisor() : idMaxDivisor();
//		double wkid=idMinDivisor();
//		return wkidToAni(wkid, k1Fraction());
//
////		final float rID=hits/(float)(refDivisor);
////		final float qID=hits/(float)(queryDivisor-contamHits);
////		final float wkid2=Tools.max(qID, rID);
////		final float ani=wkidToAni(wkid2);
////
//////		System.err.println("rid: "+wkidToAni(rID)+", qid: "+wkidToAni(qID)+", qid2: "+wkidToAni(hits/(float)(queryDivisor)));
////
////		return ani;
//	}
	
	int minDivisor(){return Tools.max(1, Tools.min(refDivisor, queryDivisor));}
	int maxDivisor(){return Tools.max(1, refDivisor, queryDivisor);}
	
	private float score0(){
		long est=useSizeEstimate ? genomeSizeEstimate() : genomeSizeKmers();
		float wkid=wkid();
		float kid=kid();
		float complt=completeness();
		float contam=contamFraction();
		float refHits=Tools.max(avgRefHits, 1f);
		float refHitMult=1f+(0.6f/(float)Math.sqrt(refHits+1));
		return (float)(refHitMult*0.2*Math.log(hits+3+(uHits()+0.25*unique2()+0.1*unique3()))
				*Math.sqrt(80*(40000+hits+uHits())*(wkid*kid*Math.pow(est*complt, 0.2)*(1-contam*0.1)))+0.1);
	}
	
	private long range(){//TODO Make sure these are calculated correctly; it seems like one divisor might be 1 higher than necessary.
		long maxA=a.array[Tools.max(0, queryDivisor-1)];
		long maxB=b.array[Tools.max(0, refDivisor-1)];
//		assert(false) : Tools.max(0, queryDivisor-1)+", "+Tools.max(0, refDivisor-1)+
//			", "+a.array[Tools.max(0, queryDivisor-1)]+", "+b.array[Tools.max(0, refDivisor-1)]+", "+Tools.max(maxA, maxB);//+"\n\n"+Arrays.toString(a.array)+"\n\n"+Arrays.toString(b.array);
		return Tools.min(maxA, maxB);
	}
	
	private static double eValue(int hits, int minDiv, int maxDiv, long range){
		if(hits>=range || maxDiv>=range){return 1.0;}
		double probHit=maxDiv/(double)range;//Saturation of range
//		double probNoHit=1-probHit;
		double eValue=Math.pow(probHit, hits);  //This is a simplification, assuming hits are very improbable.
		//Note that this does not take into account minDiv, the number of attempts...  but it should.
//		System.err.println("hits="+hits+", minDiv="+minDiv+", maxDiv="+maxDiv+", range="+range+", eValue="+eValue);
		return eValue;
	}
	
	public double eValue(){
		double eValue=eValue1()*eValue2();
//		System.err.println("eValue="+eValue);
		return eValue;
	}
	
	public double eValue1(){
		long range0=range();
		int missingBits=64-(aminoOrTranslate() ? 5 : 2)*k;
		double quantizer=1.0/(aminoOrTranslate() ? Math.pow(2, missingBits*aaBitValue) : 1L<<missingBits);
		int hits=hits1;
		int minDiv=Tools.min(qSeen1, rSeen1);
		int maxDiv=Tools.max(qSeen1, rSeen1);
		long range=Tools.max((long)Math.ceil(range0*quantizer), maxDiv);
		return eValue(hits, minDiv, maxDiv, range);
	}
	
	public double eValue2(){
		if(k2<1){return 1.0;}
		
		long range0=range();
		int missingBits=64-(aminoOrTranslate() ? 5 : 2)*k2;
		double quantizer=1.0/(aminoOrTranslate() ? Math.pow(2, missingBits*aaBitValue) : 1L<<missingBits);
		int hits=hits2();
		int minDiv=Tools.min(qSeen2(), rSeen2());
		int maxDiv=Tools.max(qSeen2(), rSeen2());
		long range=Tools.max((long)Math.ceil(range0*quantizer), maxDiv);
//		assert(false) : missingBits+", "+quantizer+", "+range0+", "+range+", "+eValue(hits, minDiv, maxDiv, range);
		return eValue(hits, minDiv, maxDiv, range);
	}
	
	public String scoreS(){
		float x=score;
		return format3(x);
	}
	
	public double depth(boolean observedToActual){
		return observedToActual ? Tools.observedToActualCoverage(depth) : depth;
	}
	
	public double depth2(boolean observedToActual){
		return observedToActual ? Tools.observedToActualCoverage(depth2) : depth2;
	}
	
	public String depthS(boolean observedToActual){
		float x=depth;
		if(observedToActual){x=(float)Tools.observedToActualCoverage(x);}
		return format3(x);
	}

	public float avgRefHits(){
		return avgRefHits;
	}

	public String avgRefHitsS(){
		return format2(avgRefHits);
	}
	
	public String depth2S(boolean observedToActual){
		float x=depth2;
		if(observedToActual){
			x=(float)(Tools.observedToActualCoverage(depth)*(depth2/depth));
		}
		return format3(x);
	}
	
	public String volumeS(){
		double x=volume()*0.001;
		return format3(x);
	}
	
	static String format3(double x){
		if(x>=999.95){
			return(""+(long)Math.round(x));
		}else if(x>=99.995){
			return String.format(Locale.ROOT, "%.1f", x);
		}else if(x>=9.9995){
			return String.format(Locale.ROOT, "%.2f", x);
		}
		return String.format(Locale.ROOT, "%.3f", x);
	}
	
	static String format2(double x){
		if(x>=999.95){
			return(""+(long)Math.round(x));
		}else if(x>=99.995){
			return String.format(Locale.ROOT, "%.1f", x);
		}else if(x>=9.9995){
			return String.format(Locale.ROOT, "%.2f", x);
		}
		return String.format(Locale.ROOT, "%.2f", x);
	}
	
	float volume(){
		return Tools.max(1f, depth)*hits;
	}
	
	@Override
	public String toString(){
		return "hits="+hits+", refDivisor="+refDivisor+", queryDivisor="+queryDivisor+", refSize="+refSize+", querySize="+querySize+
				", contamHits="+contamHits+", contam2Hits="+contam2Hits+", multiContamHits="+multiContamHits+", depth="+depth+", depth2="+depth2+", volume="+volume()+
				", hits="+hits+", multiHits="+multiHits+", unique2="+unique2+", unique3="+unique3+", noHits="+noHits+", taxID="+taxID+", taxName="+taxName;
	}
	
	/*--------------------------------------------------------------*/
	/*----------------            Getters           ----------------*/
	/*--------------------------------------------------------------*/

	public String name(){return taxName!=null ? taxName : name0()!=null ? name0() : fname()!=null ? fname() : ""+taxID();}
	public String taxName(){return taxName;}
	String name0(){return b.name0();}
	String fname(){return b.fname();}

//	public int taxID(){return b.taxID<minFakeID ? b.taxID : 0;}
	public int taxID(){return (taxID<minFakeID && taxID>=0) ? taxID : -1;}
	long imgID(){return (b.imgID>0 ? b.imgID : -1);}
	
	long genomeSizeBases(){return b.genomeSizeBases;}
	long genomeSizeKmers(){return b.genomeSizeKmers;}
	long genomeSequences(){return b.genomeSequences;}
	long genomeSizeEstimate(){return b.genomeSizeEstimate();}

	public int uHits() {return hits-multiHits;}
	
	/*--------------------------------------------------------------*/
	/*----------------         Comparators          ----------------*/
	/*--------------------------------------------------------------*/
	
	
	
	static class ScoreComparator implements Comparator<Comparison>{

		@Override
		public int compare(Comparison a, Comparison b) {
			{
				float pa=a.score, pb=b.score;
				if(pa>pb){
					return 1;
				}else if (pa<pb){
					return -1;
				}
			}
			
			int x=a.hits-b.hits;
			if(x!=0){return x;}
			x=b.minDivisor()-a.minDivisor();
			if(x!=0){return x;}
			x=b.maxDivisor()-a.maxDivisor();
			if(x!=0){return x;}
			x=b.refDivisor-a.refDivisor;
			if(x!=0){return x;}
			x=a.taxID()-b.taxID();
			if(x!=0){return x;}
			if(a.name0()!=null && b.name0()!=null){
				return a.name0().compareTo(b.name0());
			}
			if(a.taxName()!=null && b.taxName()!=null){
				return a.taxName().compareTo(b.taxName());
			}
			return 0;
		}
		
		@Override
		public String toString(){return "sortByScore";}
		
	}
	
	static class DepthComparator implements Comparator<Comparison>{

		@Override
		public int compare(Comparison a, Comparison b) {
			final float da=Tools.max(0.1f, a.depth-0.5f), db=Tools.max(0.1f, b.depth-0.5f);
			final float sa, sb;
			if(sqrt){
				sa=da*(float)Math.sqrt(a.score);
				sb=db*(float)Math.sqrt(b.score);
			}else{
				sa=da*a.score;
				sb=db*b.score;
			}
			return sa>sb ? 1 : sa<sb ? -1 : scoreComparator.compare(a, b);
		}
		
		@Override
		public String toString(){return "sortByDepth";}
		
	}
	
	static class Depth2Comparator implements Comparator<Comparison>{

		@Override
		public int compare(Comparison a, Comparison b) {
			final float da=Tools.max(0.1f, a.depth2-0.8f), db=Tools.max(0.1f, b.depth2-0.8f);
			final float sa, sb;
			if(sqrt){
				sa=da*(float)Math.sqrt(a.score);
				sb=db*(float)Math.sqrt(b.score);
			}else{
				sa=da*a.score;
				sb=db*b.score;
			}
			return sa>sb ? 1 : sa<sb ? -1 : scoreComparator.compare(a, b);
		}
		
		@Override
		public String toString(){return "sortByDepth2";}
		
	}
	
	static class VolumeComparator implements Comparator<Comparison>{

		@Override
		public int compare(Comparison a, Comparison b) {
			final float da=a.volume(), db=b.volume();
			final float sa, sb;
			if(sqrt){
				sa=da*(float)Math.sqrt(a.score);
				sb=db*(float)Math.sqrt(b.score);
			}else{
				sa=da*a.score;
				sb=db*b.score;
			}
			return sa>sb ? 1 : sa<sb ? -1 : scoreComparator.compare(a, b);
		}
		
		@Override
		public String toString(){return "sortByVolume";}
		
	}
	
	@Override
	public int compareTo(Comparison b) {
		assert(false) : "Please use comparators instead.";
		return scoreComparator.compare(this, b);
	}
	
	@Override
	public int hashCode() {
		assert(false) : "TODO";
		return super.hashCode();
	}

	public static final ScoreComparator scoreComparator=new ScoreComparator();
	public static final DepthComparator depthComparator=new DepthComparator();
	public static final Depth2Comparator depth2Comparator=new Depth2Comparator();
	public static final VolumeComparator volumeComparator=new VolumeComparator();
	private static final boolean sqrt=false;
	private static final double aaBitValue=0.86438561897747246957406388589788; //(log(20)/log(2))/5;
	
	/*--------------------------------------------------------------*/
	
	int hits(){return hits;}
	int multiHits(){return multiHits;}
	int noHits(){return noHits;}
	int unique2(){return unique2;}
	int unique3(){return unique3;}

	float depth(){return depth;}
	float depth2(){return depth2;}
	float score(){return score;}

	int contamHits(){return contamHits;}
	int contam2Hits(){return contam2Hits;}
	int multiContamHits(){return multiContamHits;}
	
	int queryDivisor(){return queryDivisor;}
	int refDivisor(){return refDivisor;}
	
	int querySize(){return querySize;}
	int refSize(){return refSize;}
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
	
	public Sketch a, b;

	String taxName;
	int taxID;
	
	private int hits;
	private int multiHits;
	private int unique2;
	private int unique3;
	private int noHits;

	private float depth;
	private float depth2;
	private float avgRefHits;
	private float score;

	private int contamHits;
	private int contam2Hits;
	private int multiContamHits;
	
	private int refDivisor;
	private int queryDivisor;
	
	private int refSize;
	private int querySize;

	private int hits1;
	private int qSeen1;
	private int rSeen1;
	
}
