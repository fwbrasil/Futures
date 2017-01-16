package io.futures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class ExceptionFutureTest {

  private <T> T get(Future<T> future) throws CheckedFutureException {
    return future.get(0, TimeUnit.MILLISECONDS);
  }

  Exception ex = new TestException();

  /*** map ***/

  @Test
  public void map() throws CheckedFutureException {
    Future<Integer> future = Future.exception(ex);
    assertEquals(future, future.map(i -> i + 1));
  }

  /*** flatMap ***/

  @Test
  public void flatMap() throws CheckedFutureException {
    Future<Integer> future = Future.exception(ex);
    assertEquals(future, future.flatMap(i -> Future.value(i + 1)));
  }

  /*** onSuccess ***/

  @Test
  public void onSuccess() throws CheckedFutureException {
    Future<Integer> future = Future.exception(ex);
    assertEquals(future, future.onSuccess(i -> {
    }));
  }

  /*** onFailure ***/

  @Test(expected = TestException.class)
  public void onFailure() throws CheckedFutureException {
    AtomicReference<Throwable> exception = new AtomicReference<>();
    Future<Integer> future = Future.<Integer>exception(ex).onFailure(exception::set);
    assertEquals(ex, exception.get());
    get(future);
  }

  @Test(expected = TestException.class)
  public void onFailureException() throws CheckedFutureException {
    Future<Integer> future = Future.<Integer>exception(ex).onFailure(ex -> {
      throw new NullPointerException();
    });
    get(future);
  }

  /*** handle ***/

  @Test
  public void handle() throws CheckedFutureException {
    Future<Integer> future = Future.<Integer>exception(ex).handle(r -> {
      assertEquals(ex, r);
      return 1;
    });
    assertEquals(new Integer(1), get(future));
  }
  
  @Test(expected = ArithmeticException.class)
  public void handleException() throws CheckedFutureException {
    Future<Integer> future = Future.<Integer>exception(ex).handle(r -> 1/0);
    get(future);
  }

  /*** rescue ***/

  @Test
  public void rescue() throws CheckedFutureException {
    Future<Integer> future = Future.<Integer>exception(ex).rescue(r -> {
      assertEquals(ex, r);
      return Future.value(1);
    });
    assertEquals(new Integer(1), get(future));
  }
  
  @Test(expected = ArithmeticException.class)
  public void rescueException() throws CheckedFutureException {
    Future<Integer> future = Future.<Integer>exception(ex).rescue(r -> Future.value(1/0));
    get(future);
  }

  /*** get ***/

  @Test(expected = TestException.class)
  public void get() throws CheckedFutureException {
    Future<Integer> future = Future.exception(ex);
    future.get(1, TimeUnit.MILLISECONDS);
  }

  @Test(expected = Error.class)
  public void getError() throws CheckedFutureException {
    Future<Integer> future = Future.exception(new Error());
    future.get(1, TimeUnit.MILLISECONDS);
  }

  @Test(expected = TestException.class)
  public void getZeroTimeout() throws CheckedFutureException {
    Future<Integer> future = Future.exception(ex);
    assertEquals(new Integer(1), future.get(0, TimeUnit.MILLISECONDS));
  }

  @Test(expected = TestException.class)
  public void getNegativeTimeout() throws CheckedFutureException {
    Future<Integer> future = Future.exception(ex);
    assertEquals(new Integer(1), future.get(-1, TimeUnit.MILLISECONDS));
  }

  /*** hashCode ***/

  @Test
  public void testHashCode() {
    assertEquals(Future.exception(ex).hashCode(), Future.exception(ex).hashCode());
  }

  @Test
  public void testHashCodeNotEquals() {
    assertNotEquals(Future.exception(ex).hashCode(), Future.exception(new NullPointerException()).hashCode());
  }

  @Test
  public void testHashCodeNull() {
    assertEquals(Future.exception(null).hashCode(), Future.exception(null).hashCode());
  }

  @Test
  public void testHashCodeNullNotEquals() {
    assertNotEquals(Future.exception(null).hashCode(), Future.exception(ex).hashCode());
  }

  /*** equals ***/

  @Test
  public void testEquals() {
    assertEquals(Future.exception(ex), Future.exception(ex));
  }

  @Test
  public void testEqualsNotEquals() {
    assertNotEquals(Future.exception(ex), Future.exception(new NullPointerException()));
  }

  @Test
  public void testEqualsNotEqualsNull() {
    assertNotEquals(Future.exception(ex), null);
  }

  @Test
  public void testEqualsNotEqualsOtherClass() {
    assertNotEquals(Future.exception(ex), "s");
  }

  @Test
  public void testEqualsNull() {
    assertEquals(Future.exception(null), Future.exception(null));
  }

  @Test
  public void testEqualsNullNotEquals() {
    assertNotEquals(Future.exception(null), Future.exception(ex));
  }
}
