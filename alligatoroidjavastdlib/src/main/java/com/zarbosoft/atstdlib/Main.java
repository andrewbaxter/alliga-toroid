package com.zarbosoft.atstdlib;

import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
  public static Sync sync = new Sync();

  public static void main(String[] args) {
    // Collections
    new GenerateClass(ArrayList.class);
    new GenerateClass(HashMap.class);
    new GenerateClass(HashSet.class);

    // System
    new GenerateClass(System.class);

    // File
    new GenerateClass(Files.class);

    // Math
    new GenerateClass(Math.class);

    // Date
    new GenerateClass(ZonedDateTime.class);
  }
}
