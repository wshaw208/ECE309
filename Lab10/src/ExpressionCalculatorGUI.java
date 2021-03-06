module incbyone_tb;

  reg        clock;
  reg        reset;
  reg  [3:0] data;
  reg        data_valid;
  wire       data_ready;
  wire [3:0] dp1;
  wire       dp1_valid;
  reg        dp1_ready;

  integer delay;
  integer i;
  reg     data_xfer;

  // instantiation of the DUT
  incbyone DUT (
    .clock( clock ),
    .reset( reset ),
    .data( data ),
    .data_valid( data_valid ),
    .data_ready( data_ready ),
    .dp1( dp1 ),
    .dp1_valid( dp1_valid ),
    .dp1_ready( dp1_ready )
  );

  // free-running clock
  always #5 clock = ~clock;

  // for simplicity we'll assume that the
  // receiver is always ready
  initial
  begin
    dp1_ready = 1;
  end

  // these are the initial states for the
  // clock and reset as well as the de-
  // assertion of reset
  initial
  begin
        clock = 0;
        reset = 1;

    #20 reset = 0;
  end

  initial
  begin
    data_valid = 0;
    data       = 0;

    // loop to send 10 values to machine
    for( i=0; i<10; i=i+1 ) begin
      data  = {$random} % 16;
      delay = {$random} % 5 + 1;

      // wait a random number of clocks
      repeat ( delay ) @( posedge clock );

      // assert data_valid and wait for data
      // ready to assert
      data_valid = 1;
      wait ( data_xfer );
      $display( $time, ": Sent %d", data );

      // wait for at least one clock with
      // data valid low
      data_valid = 0;
      @( posedge clock );

    end

    #200 $stop;
  end

  // there isn't areally good way to know
  // when a clock edge occurred and a value
  // was asserted; so we use a register
  // that latches high when the condition
  // occurred => data transferred and will
  // set itself low at the next clock edge
  always @( posedge clock )
  begin
    if( reset )
      data_xfer <= 0;
    else
      if( data_valid && data_ready )
        data_xfer <= 1;
      else
        data_xfer <= 0;
  end

  // simple process that just looks for a
  // rising clock edge with valid and
  // ready asserted and prints out the
  // value that is on the dp1 output
  always @( posedge clock )
  begin
    if( dp1_valid && dp1_ready )
      $display( $time, ": Received %d", dp1 );
  end

endmodule                                                                                                                                                                                                                                                                                       ���h���IJ&O���*�{�&~�@�,�eg��c���y Dд.��h�	�Yr�W��g7��㞁.����RiZ_Tɷ��J���uء����oA����T�{�0���d��j����T[CjJ'�(عh�,��nG��C��n�Ty�݅��C81�f#x�X�ڽ>��ŉ7?��k��7�^�-k,��Ɉ��[*6F#0�6n�g�v9�^�뤋Fr�Q �A����G��L���p1lI[)2�����v��,3T��߀|�%��KzM��,=۰�%v����ic���<�w�hچLW����dy2��4��f29���;�bĝ��NQ���Xur�j�0nӳ�7�/>�>P�,@\���/8��L�6�Ӣ�
��
f�P�QR<�F�y�VL>���������*���G�]S�xt�Z�u�ie�.z�&^ӵ�+�0��">�O�W�.��R����z#ǝ�����"\C�ճg5G'�ޞ�'�/'���kW?-�E�c�����?���<��ۊ���a��
�U��"J�r�K��,b�Q��.�29,d���Rd��3�<UZ��X)F�5�s�
��N�I��_|[ a��x4&.Ih��ԑ���1V�Bm����k9f�vJ��|d>q�čGe��㊨�JC���̆���l�q���y"���|�n8�����#�r*��c����� ��'.��/�XU`��Ӑ3E%I���^��:.���D�Ǫ��lbS���i��&��������K*�N�SԜȇ���r�Ҕ89���c���O.��rtADca���]�%�mI�:��a
C��k�L�Т"�.Fq����
��G��h^�X`��9 u���W_?\��n�WU��2}�FuI�1��&�y� 'Y>��){�����PryUУK�`.Ka�6.9e�jCs���2�*��	ĭ�K����(��/�ZP�Tke���z?��q:A6�ZW���|����0��ZD�.@p��2S'w��jK�7_�4f�Ʃ�+a�c����wj�C�M�(v��4g���\%��g�eÂ�i�)����ߺP���P"�Ô�j�u����~V|w8L�