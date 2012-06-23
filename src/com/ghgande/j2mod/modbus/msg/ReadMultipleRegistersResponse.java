//License
/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.procimg.ProcessImageFactory;
import com.ghgande.j2mod.modbus.procimg.Register;


/**
 * Class implementing a <tt>ReadMultipleRegistersResponse</tt>.
 * The implementation directly correlates with the class 0
 * function <i>read multiple registers (FC 3)</i>. It encapsulates
 * the corresponding response message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 * 
 * @author Julie (jfh@ghgande.com)
 * @version 2012-03-07
 * Added setFunctionCode() to constructors.
 */
public final class ReadMultipleRegistersResponse
    extends ModbusResponse {

  //instance attributes
  private int m_ByteCount;
  private Register[] m_Registers;

  /**
   * Constructs a new <tt>ReadMultipleRegistersResponse</tt>
   * instance.
   */
  public ReadMultipleRegistersResponse() {
    super();
    setFunctionCode(Modbus.READ_MULTIPLE_REGISTERS);
  }//constructor

  /**
   * Constructs a new <tt>ReadInputRegistersResponse</tt>
   * instance.
   *
   * @param registers the Register[] holding response registers.
   */
  public ReadMultipleRegistersResponse(Register[] registers) {
    super();
    m_Registers = registers;
    m_ByteCount = registers.length * 2;
    //set correct data length excluding unit id and fc
    setDataLength(m_ByteCount + 1);
    setFunctionCode(Modbus.READ_MULTIPLE_REGISTERS);
  }//constructor


  /**
   * Returns the number of bytes that have been read.
   * <p>
   * @return the number of bytes that have been read
   *         as <tt>int</tt>.
   */
  public int getByteCount() {
    return m_ByteCount;
  }//getByteCount

  /**
   * Returns the number of words that have been read.
   * The returned value should be half of the
   * the byte count of this
   * <tt>ReadMultipleRegistersResponse</tt>.
   * <p>
   * @return the number of words that have been read
   *         as <tt>int</tt>.
   */
  public int getWordCount() {
    return m_ByteCount / 2;
  }//getWordCount

  /**
   * Sets the number of bytes that have been returned.
   * <p>
   * @param count the number of bytes as <tt>int</tt>.
   */
  private void setByteCount(int count) {
    m_ByteCount = count;
  }//setByteCount

  /**
   * Returns the value of the register at
   * the given position (relative to the reference
   * used in the request) interpreted as unsigned short.
   * <p>
   * @param index the relative index of the register
   *        for which the value should be retrieved.
   *
   * @return the value as <tt>int</tt>.
   *
   * @throws IndexOutOfBoundsException if
   *         the index is out of bounds.
   */
  public int getRegisterValue(int index)
      throws IndexOutOfBoundsException {
    return m_Registers[index].toUnsignedShort();
  }//getRegisterValue

  /**
   * Returns the <tt>Register</tt> at
   * the given position (relative to the reference
   * used in the request).
   * <p>
   * @param index the relative index of the <tt>Register</tt>.
   *
   * @return the register as <tt>Register</tt>.
   *
   * @throws IndexOutOfBoundsException if
   *         the index is out of bounds.
   */
  public Register getRegister(int index)
      throws IndexOutOfBoundsException {

    if (index >= getWordCount()) {
      throw new IndexOutOfBoundsException();
    } else {
      return m_Registers[index];
    }
  }//getRegister

  /**
   * Returns a reference to the array of registers
   * read.
   *
   * @return a <tt>Register[]</tt> instance.
   */
  public Register[] getRegisters() {
    return m_Registers;
  }//getRegisters

  public void writeData(DataOutput dout)
      throws IOException {
    dout.writeByte(m_ByteCount);
    for (int k = 0; k < getWordCount(); k++) {
      dout.write(m_Registers[k].toBytes());
    }
  }//writeData

  public void readData(DataInput din)
      throws IOException {
    setByteCount(din.readUnsignedByte());

    m_Registers = new Register[getWordCount()];
    ProcessImageFactory pimf = ModbusCoupler.getReference().getProcessImageFactory();

    for (int k = 0; k < getWordCount(); k++) {
      m_Registers[k] = pimf.createRegister(din.readByte(), din.readByte());
    }

    //update data length
    setDataLength(getByteCount() + 1);
  }//readData
  
  public byte[] getMessage() {
	  byte result[] = null;
	  int len = 1 + getWordCount() * 2;
	  
	  result = new byte[len];
	  result[0] = (byte) m_ByteCount;
	  
	  int offset = 1;
	  for (int i = 0;i < m_Registers.length;i++) {
		  result[offset++] = m_Registers[i].toBytes()[0];
		  result[offset++] = m_Registers[i].toBytes()[1];
	  }
	  return result;
  }


}//class ReadMultipleRegistersResponse
