/*
 * Launcher.java
 *
 * Created on February 17, 2014, 5:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import com.rameses.server.BootLoader;

/**
 *
 * @author wflores
 */
public class Launcher 
{
    public static void main(String[] args) throws Exception 
    {
        Conf.load();
        BootLoader boot = new BootLoader();
        boot.start();        
    }
}
