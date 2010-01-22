/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import java.io.* ;
import java.util.HashSet ;
import java.util.Set ;

import atlas.io.PeekInputStream ;
import atlas.io.PeekReader ;
import atlas.io.StreamUTF8 ;
import atlas.iterator.Filter ;
import atlas.lib.Bytes ;
import atlas.lib.Sink ;
import atlas.lib.SinkCounting ;
import atlas.lib.SinkPrint ;
import atlas.lib.SinkWrapper ;
import atlas.lib.Tuple ;
import atlas.logging.Log ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.graph.Triple ;
import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.query.QueryExecution ;
import com.hp.hpl.jena.query.QueryExecutionFactory ;
import com.hp.hpl.jena.query.QueryFactory ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.riot.Checker ;
import com.hp.hpl.jena.riot.JenaReaderNTriples2 ;
import com.hp.hpl.jena.riot.JenaReaderTurtle2 ;
import com.hp.hpl.jena.riot.lang.LangNTriples ;
import com.hp.hpl.jena.riot.lang.LangRIOT ;
import com.hp.hpl.jena.riot.lang.LangTurtle ;
import com.hp.hpl.jena.riot.out.SinkTripleOutput ;
import com.hp.hpl.jena.riot.tokens.Tokenizer ;
import com.hp.hpl.jena.riot.tokens.TokenizerBytes ;
import com.hp.hpl.jena.riot.tokens.TokenizerText ;
import com.hp.hpl.jena.sparql.algebra.Algebra ;
import com.hp.hpl.jena.sparql.algebra.Op ;
import com.hp.hpl.jena.sparql.algebra.Transformer ;
import com.hp.hpl.jena.sparql.engine.Plan ;
import com.hp.hpl.jena.sparql.engine.binding.BindingRoot ;
import com.hp.hpl.jena.sparql.util.Context ;
import com.hp.hpl.jena.sparql.util.QueryExecUtils ;
import com.hp.hpl.jena.tdb.TDB ;
import com.hp.hpl.jena.tdb.TDBFactory ;
import com.hp.hpl.jena.tdb.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.solver.QueryEngineTDB ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.store.TransformDynamicDataset ;
import com.hp.hpl.jena.tdb.sys.SystemTDB ;
import com.hp.hpl.jena.util.FileManager ;
import com.hp.hpl.jena.util.FileUtils ;

public class RunTDB
{
    static { Log.setLog4j() ; }
    static String divider = "----------" ;
    static String nextDivider = null ;
    static void divider()
    {
        if ( nextDivider != null )
            System.out.println(nextDivider) ;
        nextDivider = divider ;
    }

    
    
    public static void main(String[] args) throws IOException
    {
        fastParse() ; System.exit(0) ;
        
        System.getProperties().setProperty("tdb:fileMode","mapped") ;
        DevCmds.tdbloader("D.ttl") ;
        System.exit(0) ;
        
        dynamicDataset() ;

    }
    
    public static void fastParse() throws IOException
    {
        if ( false )
        {
            InputStream in = null ;
            PeekInputStream pin = PeekInputStream.make(in) ;
            Tokenizer tokenizer = new TokenizerBytes(pin) ;
            //return tokenizer ;
        }

        tdb.ntriples.main("--time", "--sink", "/home/afs/Datasets/MusicBrainz/tracks-1k.nt") ;
        System.exit(0) ;

        InputStream in = new FileInputStream("/home/afs/Datasets/MusicBrainz/tracks-1k.nt") ;
        //InputStream in = new FileInputStream("D.nt") ;
        OutputStream out = new FileOutputStream("X", false) ;
        //PrintStream outp = new PrintStream(out) ;
        PrintStream outp = System.out ;
        
        //in = PeekInputStream.make(in, 10) ;
        //Reader r = FileUtils.asUTF8(in) ;
        Reader r = new StreamUTF8(in) ;
        
        PeekReader pr = PeekReader.make(r) ;
        while(true)
        {
            int x = pr.read() ;
            if ( x ==-1 ) break ;
            outp.print((char)x) ;
            //outp.flush() ;
        }
        
        outp.println("") ;
        outp.println("EXIT") ;
        outp.close();
        System.exit(0) ;
        
        //PeekReader pr = PeekReader.make(r) ;
        Tokenizer tokenizer = new TokenizerText(pr) ;
        Sink<Triple> sink = new SinkTripleOutput(System.out) ;
        LangNTriples parser = new LangNTriples(tokenizer, sink) ;
        Checker checker = new Checker(null) ;
        parser.setChecker(checker) ;
        parser.parse();
        sink.close() ;
        
        System.out.println("EXIT") ;
        System.out.println("EXIT") ;
        System.exit(0) ;
        
        //in = PeekInputStream.make(in) ;
        //Reader r = new StreamUTF8(in) ;
        
        
    }



    private static void dynamicDataset()
    {
        String desc = "FROM <http://example/dft1> FROM <http://example/dft2> FROM NAMED <http://example/g1> FROM NAMED <http://example/g2>" ; 
        
        //String qs = "SELECT * "+desc+" { { GRAPH ?g { ?s ?p ?g } } UNION { GRAPH <http://example/g1> { ?s ?p ?o } } }" ;
        String qs = "SELECT * "+desc+" { ?s ?p ?g }" ;
        //qs = "SELECT * "+desc+" { GRAPH ?g {} }" ;

        Query query = QueryFactory.create(qs) ;

        if ( false )
        {
            Dataset ds = TDBFactory.createDataset() ;
            Context context =  ARQ.getContext().copy() ;
            Plan plan = QueryEngineTDB.getFactory().create(query, ds.asDatasetGraph(), BindingRoot.create(), context) ;
            Op op = plan.getOp() ;
            System.out.print(op) ;
            System.exit(0) ;
        }
        divider() ;
        
        Op op = Algebra.compile(query) ;
        System.out.print(op) ;

        Set<Node> defaultGraph = new HashSet<Node>() ;
        Set<Node> namedGraphs = new HashSet<Node>() ;
        namedGraphs.add(Node.createURI("http://example/g1")) ;
        namedGraphs.add(Node.createURI("http://example/g2")) ;
        
        defaultGraph.add(Node.createURI("http://example/dft1")) ;
        defaultGraph.add(Node.createURI("http://example/dft2")) ;

        if ( false )
        {
            divider() ;
            System.out.println("-- Triple forms") ;
            System.out.println() ;
            Op opTriples = Transformer.transform(new TransformDynamicDataset(defaultGraph, namedGraphs, false), op) ;
            System.out.print(opTriples) ;
            divider() ;
            System.out.println("-- Triples: optimized") ;
            System.out.println() ;         
            opTriples = Algebra.optimize(opTriples) ;
            System.out.print(opTriples) ;
            
            Op op2 = Algebra.optimize(opTriples) ;
            op2 = Algebra.toQuadForm(op2) ;
            divider() ;
            System.out.println("-- Triples -> Quad") ;
            System.out.print(op2) ;
        }
        
        if ( true )
        {
            divider() ;
            System.out.println("-- Quad forms") ;
            System.out.println() ;
            Op opQuad = Algebra.toQuadForm(op) ;
            System.out.print(opQuad) ;
            opQuad = Transformer.transform(new TransformDynamicDataset(defaultGraph, namedGraphs, false), opQuad) ;
            divider() ;
            System.out.println("-- Transformed") ;
            System.out.println() ;
            System.out.print(opQuad) ;
        }

        System.exit(0) ;
    }

    static void report() 
    {
        String x ;
        try
        {
            x = FileUtils.readWholeFileAsUTF8("D.nt") ;
        } catch (IOException ex)
        {
            ex.printStackTrace(); return ;
        } 
        byte[] buff = Bytes.string2bytes(x) ;
        
        for ( int i = 0 ; i < 1000000 ; i++ )
        {
            
             
            if ( i%1000 == 1 )
                System.out.print(".") ;
            
            if ( i%100000 == 1 )
                System.out.println() ;
            
            InputStream in = new ByteArrayInputStream(buff) ;
            JenaReaderNTriples2.parse(in) ;  
        }
        System.out.println() ;
        System.out.println("Finished") ;
        System.exit(0) ;

    }
    
    static void genericQuery(Dataset ds)
    {
        String queryString = "prefix : <http://example/> SELECT * { ?s ?p 123 }" ;
        Query query = QueryFactory.create(queryString) ;
        QueryExecution qExec = QueryExecutionFactory.create(query, ds) ;
        QueryExecUtils.executeQuery(query, qExec) ;
        qExec.close();
    }
    
    static void tupleFilter()
    {
        Dataset ds = TDBFactory.createDataset("DB") ;
        DatasetGraphTDB dsg = (DatasetGraphTDB)(ds.asDatasetGraph()) ;

        final NodeTable nodeTable = dsg.getQuadTable().getNodeTupleTable().getNodeTable() ;
        final NodeId target = nodeTable.getNodeIdForNode(Node.createURI("http://example/graph2")) ;

        System.out.println("Filter: "+target) ;
        
        Filter<Tuple<NodeId>> filter = new Filter<Tuple<NodeId>>() {
            
            public boolean accept(Tuple<NodeId> item)
            {
                
                // Reverse the lookup as a demo
                Node n = nodeTable.getNodeForNodeId(target) ;
                
                //System.err.println(item) ;
                if ( item.size() == 4 && item.get(0).equals(target) )
                {
                    System.out.println("Reject: "+item) ;
                    return false ;
                }
                System.out.println("Accept: "+item) ;
                return true ;
            } } ;
            
            
        TDB.getContext().set(SystemTDB.symTupleFilter, filter) ;

        String qs = "SELECT * { { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }}" ;
        //String qs = "SELECT * { GRAPH ?g { ?s ?p ?o } }" ;
        
        DevCmds.tdbquery("--tdb=tdb.ttl", qs) ;
    }
    
    public static void turtle2() throws IOException
    {
        // Also tdb.turtle.
        //        TDB.init();
        //        RDFWriter w = new JenaWriterNTriples2() ;
        //        Model model = FileManager.get().loadModel("D.ttl") ;
        //        w.write(model, System.out, null) ;
        //        System.exit(0) ;

        InputStream input = new FileInputStream("D.ttl") ;
        JenaReaderTurtle2.parse(input) ;
        System.out.println("END") ;
        System.exit(0) ;
    }
    
    static class SinkGapper<T> extends SinkWrapper<T>
    {
        public SinkGapper(Sink<T> sink)
        {
            super(sink) ;
        }
        
        @Override
        public void send(T item)
        {
            super.send(item) ;
            System.out.println("--") ;
        }
    }
    
    public static void streamInference()
    {
        Model m = FileManager.get().loadModel("V.ttl") ;
        
        SinkCounting<Triple> outputSink = new SinkCounting<Triple>(new SinkPrint<Triple>()) ;
        
        SinkCounting<Triple> inputSink1 = new SinkCounting<Triple>(new InferenceExpander(outputSink, m)) ;
        // Add gaps between parser triples. 
        Sink<Triple> inputSink2 = new SinkGapper<Triple>(inputSink1) ;
        
        Sink<Triple> inputSink = inputSink2 ;
        
        Tokenizer tokenizer = new TokenizerText(PeekReader.open("D.ttl")) ;
        LangRIOT parser = new LangTurtle("http://base/", tokenizer, inputSink) ;
        
        parser.parse() ;
        inputSink.flush() ;

        System.out.println() ;
        System.out.printf("Input  =  %d\n", inputSink1.getCount()) ;
        System.out.printf("Total  =  %d\n", outputSink.getCount()) ;
    }
}

/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */