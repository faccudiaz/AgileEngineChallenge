package com.agileengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
    private static String CHARSET_NAME = "utf8";

	
	public static void main(String[] args) {
		getElementFromPages("./samples/sample-0-origin.html", "./samples/sample-1-evil-gemini.html", "a[title=\"Make-Button\"]");
	}
	
	public static void getElementFromPages(String input_origin_file_path, String input_origin_other_sample_file_path, String cssQuery) {
		File input = new File(input_origin_file_path);
		try {
			Document doc = Jsoup.parse(input, "UTF-8");
			Element originElement = doc.getElementById("make-everything-ok-button");
			showElementAtributes(originElement);
//			Element sameElementOtherFile = findElementInOtherSampleFile(originElement, input_origin_other_sample_file_path);
//			showElementAtributes(sameElementOtherFile);
			
			Optional<Elements> elementsOpt = findElementsByQuery(new File(input_origin_other_sample_file_path), cssQuery);
			Optional<Elements> elementsOptOther = findElementsByQuery(new File(input_origin_other_sample_file_path), cssQuery);

	        showOptional(elementsOpt);
	        showOptional(elementsOptOther);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showOptional(Optional<Elements> elementsOpt) {
		Optional<List<String>> elementsAttrsOpts = elementsOpt.map(buttons ->
		        {
		            List<String> stringifiedAttrs = new ArrayList<>();

		            buttons.iterator().forEachRemaining(button ->
		                    stringifiedAttrs.add(
		                            button.attributes().asList().stream()
		                                    .map(attr -> attr.getKey() + " = " + attr.getValue())
		                                    .collect(Collectors.joining(", "))));

		            return stringifiedAttrs;
		        }
		);

		System.out.println(elementsAttrsOpts.get());
	}
	
	private static Optional<Elements> findElementsByQuery(File htmlFile, String cssQuery) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            return Optional.of(doc.select(cssQuery));

        } catch (IOException e) {
        	e.printStackTrace();
        	return Optional.empty();
        }
    }

	private static void showElementAtributes(Element element) {
		List<Attribute> elementAttributes = getElementAttributes(element);
		for (Attribute attribute : elementAttributes) {
			System.out.println("Attribute name: " + attribute.getKey() + ". Value: " + attribute.getValue());
		}
	}

	private static List<Attribute> getElementAttributes(Element element) {
		return element.attributes().asList();
	}
	
	private static Element findElementInOtherSampleFile(Element originElement, String input_other_sample_file_path) {
		List<Attribute> elementAttributes = getElementAttributes(originElement);
//		Stream<Attribute> attributesStream = elementAttributes.stream();
		String cssQuery = "a[title=\"Make-Button\"]";
        Optional<Elements> elementsOpt = findElementsByQuery(new File("./samples/sample-1-evil-gemini.html"), cssQuery);
        System.out.println(elementsOpt.get());
		File input = new File(input_other_sample_file_path);
		Element elementFound = null;
		try {
			Document doc = Jsoup.parse(input, CHARSET_NAME);
			Element content = doc.getElementsByClass("panel-default").first();
			Elements links = content.getElementsByTag("a");
			for (Element link : links) {
				for (Attribute attributeInElement : elementAttributes) {
					for (Attribute attributeInLink : link.attributes()) {
						if (attributeInLink.getValue().equals(attributeInElement.getValue())) {
							System.out.println("Element recovered: " + link);
							System.out.println("Attribute: " + attributeInElement.getKey() + " with value: " + attributeInElement.getValue());
							elementFound = link;
						}
						
					}
					
				}
				System.out.println(link.getAllElements());
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		return elementFound;
	}

}
