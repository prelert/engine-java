/****************************************************************************
 *                                                                          *
 * Copyright 2015-2016 Prelert Ltd                                          *
 *                                                                          *
 * Licensed under the Apache License, Version 2.0 (the "License");          *
 * you may not use this file except in compliance with the License.         *
 * You may obtain a copy of the License at                                  *
 *                                                                          *
 *    http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                          *
 * Unless required by applicable law or agreed to in writing, software      *
 * distributed under the License is distributed on an "AS IS" BASIS,        *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and      *
 * limitations under the License.                                           *
 *                                                                          *
 ***************************************************************************/

package com.prelert.job;

/**
 * Interface to be used for cases where we want to be able to receive passwords
 * either encrypted or in plain text, yet encrypt the plain text ones as soon
 * as possible after receipt.
 */
public interface PasswordStorage
{
    /**
     * Get the plain text password.  If this interface is used as expected
     * then this should be expected to return <code>null</code> most of the time.
     */
    String getPassword();

    /**
     * Set the plain text password.
     */
    void setPassword(String password);

    /**
     * Get the encrypted password.  A class outside this package is responsible
     * for decryption.
     */
    String getEncryptedPassword();

    /**
     * Set the encrypted password.  This must have been encrypted using the
     * appropriate methodology and key.
     */
    void setEncryptedPassword(String encryptedPassword);
}
