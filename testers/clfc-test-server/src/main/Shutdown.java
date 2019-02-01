/*
 * Shutdown.java
 *
 * Created on February 17, 2014, 5:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

/**
 *
 * @author wflores
 */
public class Shutdown 
{
    public static void main(String[] args) {
        Conf.load();
        com.rameses.server.Shutdown.main(args); 
    }
}
