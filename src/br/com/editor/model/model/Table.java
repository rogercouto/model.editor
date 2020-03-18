package br.com.editor.model.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe que representa o esquema da tabela ou fonte de dados
 * Suporta esquemas de dados nao normalizados
 * @author roger
 */
public class Table extends Element implements Cloneable{

	private static final long serialVersionUID = -4462180314772065989L;

	private List<Element> elements = new ArrayList<>();
	private HashMap<String, Integer> niMap = new HashMap<String, Integer>();
	private boolean haveNestedTables = false;
	private boolean havePrimaryKey = false;
	private Column surrogateKey = null;
	
	private boolean migrateData = false;
	
	public Table() {
		super();
	}

	public Table(String name) {
		super(name);
	}

	public Table(String name, boolean createSurrogateKey) {
		super(name);
	}

	/**
	 * Adiciona uma coluna na tabela
	 * @param column - coluna a ser adicionada
	 */
	public void addColumn(Column column){
		/*
		if (niMap.containsKey(column.getName()))
			throw new RuntimeException(
					String.format("addColumn: column with name %s already in table %s",
							column.getName(), getName()));
							*/
		if (!niMap.containsKey(column.getName())){
			niMap.put(column.getName(), elements.size());
			column.setTable(this);
			elements.add(column);
			if (column.isPrimaryKey())
				havePrimaryKey = true;
			if (column.isSurrogateKey())
				surrogateKey = column;
		}
	}

	/**
	 * Adiciona uma tabela aninhada ao esquema
	 * @param table - tabela a ser adicioanda
	 */
	public void addTable(Table table){
		if (niMap.containsKey(table.getName()))
			throw new RuntimeException(
					String.format("addColumn: column with name %s already in table %s",
							table.getName(), getName()));
		niMap.put(table.getName(), elements.size());
		elements.add(table);
		haveNestedTables = true;
	}

	/**
	 * Retorna os elementos de uma tabela
	 * @return lista de elementos
	 */
	public List<Element> getElements(){
		return this.elements;
	}

	/**
	 * Retorna os elementos de uma tabela que sao colunas
	 * @return lista de colunas
	 */
	public List<Column> getColumns(){
		return this.elements.stream()
				.filter(c -> c.getClass().equals(Column.class))
				.map(c -> (Column)c)
				.collect(Collectors.toList());
	}

	/**
	 * Retorna os elementos de uma tabela que sao colunas
	 * @return lista de colunas
	 */
	public List<Column> getColumns(boolean withSurrogateKeys){
		return this.elements.stream()
				.filter(c -> c.getClass().equals(Column.class))
				.map(c -> (Column)c)
				.filter(c -> c.isSurrogateKey()==withSurrogateKeys)
				.collect(Collectors.toList());
	}

	/**
	 * Retorna os elementos de uma tabela que sao colunas
	 * @return lista de colunas
	 */
	public List<Column> getForeignKeys(){
		return this.elements.stream()
				.filter(c -> c.getClass().equals(Column.class))
				.map(c -> (Column)c)
				.filter(c -> c.getForeignKey() != null)
				.collect(Collectors.toList());
	}

	/**
	 * Retorna os elementos de uma tabela que sao tabelas aninhadas
	 * @return lista de tabelas
	 */
	public List<Table> getSubtables(){
		return this.elements.stream()
				.filter(t -> t.getClass().equals(Table.class))
				.map(t -> (Table)t)
				.collect(Collectors.toList());
	}

	/**
	 * Retorna o indice do elemento
	 * @param elementName: nome do elemento
	 * @return indice do elemento
	 */
	public int getElementIndex(String elementName){
		Integer index = niMap.get(elementName);
		if (index == null)
			return -1;
		return index.intValue();
	}

	public int getColumnIndexIfExists(String columnName){
		Integer index = niMap.get(columnName);
		if (index == null)
			return -1;
		return index.intValue();
	}

	/**
	 * Retorna o elemento conforme o indice
	 * @param index: indice do elemento
	 * @return elemento
	 */
	public Element getElement(int index){
		return elements.get(index);
	}

	/**
	 * Retorna o elemento conforme o nome
	 * @param elementName: nome do elemento
	 * @return elemento
	 */
	public Element getElement(String elementName){
		return elements.get(getElementIndex(elementName));
	}

	/**
	 * Retorna a coluna conforme o indice
	 * @param index: indice da coluna
	 * @return coluna
	 */
	public Column getColumn(int index){
		Element e = elements.get(index);
		if (!e.getClass().equals(Column.class))
			throw new RuntimeException(
					String.format("getColumn: element with index %d is not a column!",index));
		return (Column)e;
	}

	public Column getColumnIFExists(int index){
		if (index < elements.size()){
			Element e = elements.get(index);
			if (e.getClass().equals(Column.class))
				return (Column)e;
		}
		return null;
	}

	public Column getColumnIFExists(String columnName){
		Integer index = niMap.get(columnName);
		if (index != null && elements.get(index).getClass().equals(Column.class)){
			return (Column)elements.get(index);
		}
		return null;
	}

	/**
	 * Retorna a coluna conforme o nome
	 * Caso o elemento nao seja uma coluna ira lancar um erro de execucao
	 * @param columnName: nome da coluna
	 * @return coluna
	 */
	public Column getColumn(String columnName){
		int index = getElementIndex(columnName);
		if (index < 0)
			return null;
		Element e = elements.get(index);
		if (!e.getClass().equals(Column.class))
			throw new RuntimeException(
					String.format("getColumn: element with name %s is not a column!",columnName));
		return (Column)e;
	}

	/**
	 * Retorna a tabela conforme o nome
	 * Caso o elemento nao seja uma tabela ira lancar um erro de execucao
	 * @param index: indice da tabela
	 * @return tabela
	 */
	public Table getSubtable(int index){
		Element e = elements.get(index);
		if (!e.getClass().equals(Table.class))
			throw new RuntimeException(
					String.format("getColumn: element with index %d is not a table!",index));
		return (Table)e;
	}

	/**
	 * Retorna uma tabela conforme o nome
	 * @param tableName
	 * @return tabela
	 */
	public Table getSubtable(String tableName){
		Element e = elements.get(getElementIndex(tableName));
		if (!e.getClass().equals(Table.class))
			throw new RuntimeException(
					String.format("getColumn: element with name %s is not a table!",tableName));
		return (Table)e;
	}

	public void removeColumn(int index){
		elements.remove(index);
	}
	
	public void removeColumn(String columnName){
		Element e = elements.get(getElementIndex(columnName));
		if (!e.getClass().equals(Column.class))
			throw new RuntimeException(
					String.format("removeColumn: element with name %s is not a column!",columnName));
		int index = getElementIndex(columnName);
		elements.remove(index);
	}

	public void removeColumns(Collection<Column> columns){
		for (Column column : columns) {
			removeColumn(column.getName());
		}
		niMap = new HashMap<String, Integer>();
		int index = 0;
		for (Element element : elements) {
			niMap.put(element.getName(), index++);
		}
	}
	/**
	 * Adiciona uma coluna no inicio da tabela
	 * @param column: coluna a ser adicionada
	 */
	public void addColumnFirst(Column column) {
		elements.add(0, column);
		niMap = new HashMap<String, Integer>();
		int index = 0;
		for (Element element : elements) {
			niMap.put(element.getName(), index++);
		}
		if (column.isPrimaryKey())
			havePrimaryKey = true;
	}

	/**
	 * Adiciona uma lista de colunas no inicio da tabela
	 * @param columns: colunas a ser adicionada
	 */
	public void addColumnsFirst(List<Column> columns) {
		for (Column c : columns) {
			elements.add(columns.indexOf(c), c);
			if (c.isPrimaryKey())
				havePrimaryKey = true;
		}
		niMap = new HashMap<String, Integer>();
		int index = 0;
		for (Element element : elements) {
			niMap.put(element.getName(), index++);
		}

	}

	/**
	 * Retorna uma contagem de elementos da tabela
	 * @return total de elementos
	 */
	public int getElementCount(){
		return elements.size();
	}

	/**
	 * Indica se a tabela possui outras tabelas aninhadas
	 * @return boleano
	 */
	public boolean haveNestedTables(){
		//return elements.stream().filter(e -> e.getClass().equals(Table.class)).count() > 0;
		return haveNestedTables;
	}

	public boolean havePrimaryKey() {
		return havePrimaryKey;
	}
	
	public boolean haveSurrogateKey() {
		/*
		Optional<Column> op = elements.stream()
				.filter(e -> e.getClass().equals(Column.class))
				.map(c -> (Column)c)
				.filter(c -> c.isSurrogateKey())
				.findFirst();
		return (op.isPresent());
		*/
		return (surrogateKey != null);
	}

	public List<Column> getPrimaryKeys(){
		return elements.stream()
				.filter(e -> e.getClass().equals(Column.class))
				.map(c -> (Column)c)
				.filter(c -> c.isPrimaryKey())
				.collect(Collectors.toList());
	}

	@Deprecated
	public List<Column> getSurrogateKeys(){
		return elements.stream()
				.filter(e -> e.getClass().equals(Column.class))
				.map(c -> (Column)c)
				.filter(c -> c.isSurrogateKey())
				.collect(Collectors.toList());
	}

	public Column getSurrogateKey(){
		Optional<Column> op = elements.stream()
				.filter(e -> e.getClass().equals(Column.class))
				.map(c -> (Column)c)
				.filter(c -> c.isSurrogateKey())
				.findFirst();
		if (op.isPresent())
			return op.get();
		return null;
	}

	private String elementsToString(int spaces){
		StringBuilder builder = new StringBuilder();
		for (Element element : elements) {
			if (elements.indexOf(element) > 0)
				builder.append(",\n");
			else
				builder.append("\n");
			for (int i = 0; i < spaces; i++) {
				builder.append(' ');
			}
			if (element.getClass().equals(Table.class)){
				builder.append("   ");
				builder.append(element.getName());
				builder.append("   (");
				Table sElement = (Table)element;
				builder.append(sElement.elementsToString(spaces+3));
				for (int i = 0; i < spaces; i++) {
					builder.append(' ');
				}
				builder.append("   )");
			}else{
				builder.append(" ");
				Column sColumn = (Column)element;
				if (sColumn.isPrimaryKey())
					builder.append("*");
				else
					builder.append(" ");
				if (sColumn.getForeignKey() != null)
					builder.append("@");
				else
					builder.append(" ");
				builder.append(element.getName());
				builder.append(" : ");
				if (sColumn.getDbType() != null){
					builder.append(sColumn.getDbType());
					builder.append("|");
				}
				if (sColumn.getType() != null)
					builder.append(sColumn.getType().getSimpleName());
				if (sColumn.getSize() != null && sColumn.getSize() > 0){
					builder.append("(");
					builder.append(sColumn.getSize());
					builder.append(")");
				}
				if (sColumn.isSurrogateKey())
					builder.append(" AA");
				if (sColumn.isUnique())
					builder.append(" U");
				if (sColumn.getForeignKey() != null){
					builder.append(" refrences(");
					builder.append(sColumn.getForeignKey().getName());
					builder.append(")");
				}
			}
		}
		builder.append("\n");
		return builder.toString();
	}
	public String toString(int spaces){
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < spaces; i++) {
			builder.append(' ');
		}
		builder.append(getName());
		builder.append("(");
		builder.append(this.elementsToString(spaces+3));
		builder.append(")");
		return builder.toString();
	}

	/**
	 * Impressao do esquema
	 */
	@Override
	public String toString(){
		return toString(0);
	}

	@Override
	public Table clone(){
		Table table = new Table(getName());
		table.setName(getName());
		table.setMigrateData(this.isMigrateData());
		for (Element element : elements) {
			if (element.getClass().equals(Table.class)){
				Table sub = (Table)element;
				table.addTable(sub.clone());
			}else{
				Column eColumn = (Column)element;
				table.addColumn(eColumn.clone());
			}
		}
		return table;
	}

	protected void havePrimaryKeys(boolean b) {
		havePrimaryKey = b;
	}

	public void setElementName(String oldName, String newName){
		Integer index =niMap.get(oldName);
		niMap.remove(oldName);
		niMap.put(newName, index);
		elements.get(index).setName(newName);
	}

	public Integer[] getPkIndices(){
		List<Integer> indices = new LinkedList<>();
		getPrimaryKeys().forEach(pk->{
			indices.add(niMap.get(pk.getName()));
		});
		return indices.toArray(new Integer[indices.size()]);
	}

	public void testIndexes(){
		niMap.forEach((s,i)->{
			System.err.println(s+" -> i: "+i);
		});
	}

	public void addElement(Element e){
		elements.add(e);
	}

	public boolean isMigrateData() {
		return migrateData;
	}

	public void setMigrateData(boolean migrateData) {
		this.migrateData = migrateData;
	}

	public String getClassName() {
		return getNormalizatedName(true);
	}

}
