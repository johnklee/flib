/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.dp.observer;

/**
 *
 * @author John-Lee
 */
public interface ISubject extends IObservable{
    public void register(IObserver o);
    public void removeRegister(IObserver o);
    public void notifyObservers(String msg);
}
