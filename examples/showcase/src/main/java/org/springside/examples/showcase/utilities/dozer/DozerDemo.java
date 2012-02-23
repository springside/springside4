package org.springside.examples.showcase.utilities.dozer;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springside.modules.mapper.BeanMapper;

public class DozerDemo {

	/**
	 * 演示Dozer如何只要属性名相同，可以罔顾属性类型是基础类型<->String的差别，甚至完全另一种DTO的差别。
	 */
	@Test
	public void mapByClassName() {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setName("car");
		productDTO.setPrice("200");
		PartDTO partDTO = new PartDTO();
		partDTO.setName("door");
		productDTO.setParts(new PartDTO[] { partDTO });

		Product product = BeanMapper.map(productDTO, Product.class);

		assertEquals("car", product.getName());
		assertEquals(new Double(200), product.getPrice());
		assertEquals("door", product.getParts().get(0).getName());
	}

	@Test
	public void copy() {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setName("car");
		productDTO.setPrice("200");
		PartDTO partDTO = new PartDTO();
		partDTO.setName("door");
		productDTO.setParts(new PartDTO[] { partDTO });

		Product product = new Product();
		product.setName("horse");
		product.setWeight(new Double(20));

		BeanMapper.copy(productDTO, product);

		assertEquals("car", product.getName());
		assertEquals(new Double(200), product.getPrice());
		assertEquals("door", product.getParts().get(0).getName());
		//DTO中没有的属性值,在Product中被保留
		assertEquals(new Double(20), product.getWeight());
	}

	public static class Product {
		private String name;
		private Double price;
		private List<Part> parts;
		//DTO中没有的属性
		private Double weight;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public List<Part> getParts() {
			return parts;
		}

		public void setParts(List<Part> parts) {
			this.parts = parts;
		}

		public Double getWeight() {
			return weight;
		}

		public void setWeight(Double weight) {
			this.weight = weight;
		}

	}

	public static class Part {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class ProductDTO {

		private String name;
		//类型为String 而非 Double
		private String price;
		//类型为Array而非List, PartDTO而非Part
		private PartDTO[] parts;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public PartDTO[] getParts() {
			return parts;
		}

		public void setParts(PartDTO[] parts) {
			this.parts = parts;
		}
	}

	public static class PartDTO {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
