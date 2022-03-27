package de.fhkiel.ki.cathedral;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TurnableTest {

  @Test
  void getRealDirection() {
    assertThat(Turnable.No.getRealDirection(Direction._0)).isEqualTo(Direction._0);
    assertThat(Turnable.No.getRealDirection(Direction._90)).isEqualTo(Direction._0);
    assertThat(Turnable.No.getRealDirection(Direction._180)).isEqualTo(Direction._0);
    assertThat(Turnable.No.getRealDirection(Direction._270)).isEqualTo(Direction._0);

    assertThat(Turnable.Half.getRealDirection(Direction._0)).isEqualTo(Direction._0);
    assertThat(Turnable.Half.getRealDirection(Direction._90)).isEqualTo(Direction._90);
    assertThat(Turnable.Half.getRealDirection(Direction._180)).isEqualTo(Direction._0);
    assertThat(Turnable.Half.getRealDirection(Direction._270)).isEqualTo(Direction._90);

    assertThat(Turnable.Full.getRealDirection(Direction._0)).isEqualTo(Direction._0);
    assertThat(Turnable.Full.getRealDirection(Direction._90)).isEqualTo(Direction._90);
    assertThat(Turnable.Full.getRealDirection(Direction._180)).isEqualTo(Direction._180);
    assertThat(Turnable.Full.getRealDirection(Direction._270)).isEqualTo(Direction._270);
  }
}