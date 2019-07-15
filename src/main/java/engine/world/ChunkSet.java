package engine.world;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import engine.maths.Pair;

public class ChunkSet implements Iterable<Chunk>{
    private Chunk[][] chunks;
    private Iterator<Pair> setIter;
    
    public ChunkSet(Iterator<Pair>setIter,Chunk[][] chunks) {
        this.chunks=chunks;
        this.setIter=setIter;
    }

    @Override
    public Iterator<Chunk> iterator() {
        class iter implements Iterator<Chunk>{

            @Override
            public boolean hasNext() {
                return setIter.hasNext();
            }

            @Override
            public Chunk next() {
                Pair pair = (Pair)setIter.next();
                return chunks[pair.first][pair.second];
            }
            
        }
        
        return new iter();
    }
    
    
}
