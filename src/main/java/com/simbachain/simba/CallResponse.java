/*
 * Copyright (c) 2023 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.simbachain.simba;

/**
 * The return value of a call to a method using POST.
 * The response contains a unique request identifier. This can either be
 * an ID generated for the request, or a transaction hash associated with the request
 * if the request is creating a transaction.
 * <p>
 * Additionally the response may contain a state attribute and an error string.
 */
public class CallResponse {

    private final String requestIdentifier;

    private String status;

    private String error;

    public CallResponse(String requestIdentitier) {
        this.requestIdentifier = requestIdentitier;
    }

    public String getRequestIdentifier() {
        return requestIdentifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallResponse{");
        sb.append("requestIdentifier='")
          .append(requestIdentifier)
          .append('\'');
        sb.append(", status='")
          .append(status)
          .append('\'');
        sb.append(", error='")
          .append(error)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
