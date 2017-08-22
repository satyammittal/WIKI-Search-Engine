import java.util.*;
public class Pair<A, B> implements Comparable<Object>{
    private A first;
    private B second;

    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            return 
            ((  this.first == otherPair.first ||
                ( this.first != null && otherPair.first != null &&
                  this.first.equals(otherPair.first))) &&
             (  this.second == otherPair.second ||
                ( this.second != null && otherPair.second != null &&
                  this.second.equals(otherPair.second))) );
        }

        return false;
    }
    @Override
    public int compareTo(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            if(this.first instanceof Integer)
            {
            if((Integer)this.first==(Integer)otherPair.first)
            {
                return 0;
            }
            else if((Integer)this.first>(Integer)otherPair.first){
                return 1;
            }
            else
                return -1;
            }
        }
        return -1;
    }
    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }
}