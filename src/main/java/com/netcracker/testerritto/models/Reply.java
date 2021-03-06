package com.netcracker.testerritto.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reply extends ObjectEav {

    private BigInteger resultId;
    @JsonDeserialize(as = ArrayList.class, contentAs = Answer.class)
    private List<Answer> replyList;

    public Reply(){}

    public Reply(BigInteger resultId, List<Answer>replyList){
        this.resultId = resultId;
        this.replyList = replyList;
    }

    public BigInteger getResultId() {
        return resultId;
    }

    public void setResultId(BigInteger resultId) {
        this.resultId = resultId;
    }

    public List<Answer> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Answer> replyList) {
        this.replyList = replyList;
    }

    public BigInteger getReplyId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reply reply = (Reply) o;
        return getResultId().equals(reply.getResultId()) &&
            getReplyList().equals(reply.getReplyList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getResultId(), getReplyList());
    }

    @Override
    public String toString() {
        return "Reply{" +
            "resultId=" + resultId +
            ", replyList=" + replyList +
            ", id=" + id +
            '}';
    }
}