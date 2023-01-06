package com.argusoft.who.emcare.web.indicators.indicator.entity;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 29/12/22  11:22 am
 */
@Entity
@Table(name = "indicator_denominator_equation")
public class IndicatorDenominatorEquation extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "denominator_id", nullable = false)
    private Long denominatorId;

    @Column(name = "code_id")
    private Long codeId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "condition")
    private String condition;

    @Column(name = "value")
    private String value;

    @Column(name = "value_type")
    private String valueType;

    @Column(name = "eq_identifier")
    private String eqIdentifier;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id", nullable = false)
    private Indicator denominatorIndicator;

    public Long getDenominatorId() {
        return denominatorId;
    }

    public void setDenominatorId(Long denominatorId) {
        this.denominatorId = denominatorId;
    }

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Indicator getDenominatorIndicator() {
        return denominatorIndicator;
    }

    public void setDenominatorIndicator(Indicator denominatorIndicator) {
        this.denominatorIndicator = denominatorIndicator;
    }

    public String getEqIdentifier() {
        return eqIdentifier;
    }

    public void setEqIdentifier(String eqIdentifier) {
        this.eqIdentifier = eqIdentifier;
    }
}