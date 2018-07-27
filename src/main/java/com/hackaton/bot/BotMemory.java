package com.hackaton.bot;

import lombok.Getter;
import lombok.Setter;

/**
 * Secuencia.
 *
 * @author lballena.
 */
@Getter
@Setter
public class BotMemory {

  private boolean init = true;

  private String telephone;

  private boolean dni = false;

}
