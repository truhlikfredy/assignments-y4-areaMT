package antonkrug.eu;

/**
 * Simple container for tuples, to be able to return back from methods a return
 * code and return string with message at the same time.
 * 
 * @author  Anton Krug
 * @date    26.9.2016
 * @version 2
 */
public class Pair<F, S> {

  private final F first;
  private final S second;


  /**
   * Constructor populating both fields
   * 
   * @param first
   * @param second
   */
  public Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }


  /**
   * Returns first field
   * @return
   */
  public F getFirst() {
    return first;
  }


  /**
   * Returns second field
   * @return
   */
  public S getSecond() {
    return second;
  }


  @Override
  public int hashCode() {
    return first.hashCode() ^ second.hashCode();
  }


  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair)) return false;
    Pair pairo = (Pair) o;
    return this.first.equals(pairo.getFirst()) && this.second.equals(pairo.getSecond());
  }


  @Override
  public String toString() {
    return "Pair [first=" + first + ", second=" + second + "]";
  }
  

}
