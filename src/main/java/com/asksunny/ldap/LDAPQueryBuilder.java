package com.asksunny.ldap;


public class LDAPQueryBuilder {

	private enum ConditionOperator {
		AND, OR
	};

	private class Condition {
		ConditionOperator op = null;
		String filterString = null;
		Condition nestedCondition = null;

		public void setOp(ConditionOperator op) {
			this.op = op;
		}

		public void setFilterString(String filterString) {
			this.filterString = filterString;
		}

		public void setFilterString(String name, String value) {
			this.filterString = String.format("%s=%s", name, value);
		}

		public String toFilterString() {
			if (nestedCondition == null) {
				return String.format("(%s)", this.filterString);
			} else {
				String opstr = (op == null) ? "&"
						: ((op == ConditionOperator.AND) ? "&" : "|");
				String ret = String.format("(%s%s(%s))", opstr,
						nestedCondition.toFilterString(), this.filterString);
				return ret;
			}

		}

		public void setNestedCondition(Condition nestedCondition) {
			if (this.nestedCondition == null) {
				this.nestedCondition = nestedCondition;
			} else {
				this.nestedCondition.setNestedCondition(nestedCondition);
			}
		}

	}

	public static LDAPQueryBuilder newInstance() {
		return new LDAPQueryBuilder();
	}

	private Condition condition;

	private LDAPQueryBuilder() {

	}

	public LDAPQueryBuilder filter(String name, String value) {
		if (condition != null) {
			throw new IllegalArgumentException(
					"Filter already exists, use and or or operation instead");
		}
		condition = new Condition();
		condition.setFilterString(name, value);
		return this;
	}

	public LDAPQueryBuilder filter(String filterString) {
		if (condition != null) {
			throw new IllegalArgumentException(
					"Filter already exists, use and or or operation instead");
		}
		condition = new Condition();
		condition.setFilterString(filterString);
		return this;
	}

	public LDAPQueryBuilder and(String name, String value) {

		Condition newcon = new Condition();
		newcon.setFilterString(name, value);
		Condition nested = this.condition;
		this.condition = newcon;
		nested.setOp(ConditionOperator.AND);
		condition.setNestedCondition(nested);
		return this;
	}

	public LDAPQueryBuilder or(String name, String value) {
		Condition newcon = new Condition();
		newcon.setFilterString(name, value);
		Condition nested = this.condition;
		this.condition = newcon;
		nested.setOp(ConditionOperator.OR);
		condition.setNestedCondition(nested);
		return this;
	}

	public LDAPQueryBuilder and(String queryString) {
		Condition newcon = new Condition();
		newcon.setFilterString(queryString);
		Condition nested = this.condition;
		this.condition = newcon;
		nested.setOp(ConditionOperator.AND);
		condition.setNestedCondition(nested);
		return this;
	}

	public LDAPQueryBuilder or(String queryString) {
		Condition newcon = new Condition();
		newcon.setFilterString(queryString);
		Condition nested = this.condition;
		this.condition = newcon;
		nested.setOp(ConditionOperator.OR);
		condition.setNestedCondition(nested);
		return this;
	}

	public String build() {
		return condition.toFilterString();
	}

}
