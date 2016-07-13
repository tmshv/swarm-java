import processing.core.PVector;

class GeometryUtils{
	/**
	* Calculate projection of vertex on the line
	**/
	public static PVector projectVertexOnLine(PVector vertex, PVector lineVertex1, PVector lineVertex2) {
	  PVector a = new PVector(vertex.x - lineVertex1.x, vertex.y - lineVertex1.y);
	  PVector b = new PVector(lineVertex2.x - lineVertex1.x, lineVertex2.y - lineVertex1.y);
	  
	  //Формула проецирования вектора a на вектор b:
	  float dot_product = (a.x * b.x + a.y * b.y);
	  float b_length_quad = b.x * b.x + b.y * b.y;
	  
	  PVector p = new PVector(
	    ( dot_product / b_length_quad ) * b.x, 
	    ( dot_product / b_length_quad ) * b.y
	  );
	  
	  return new PVector(
	    lineVertex1.x + p.x, 
	    lineVertex1.y + p.y
	  );
	}

	/**
	* define type of point relativly line
	* point: {x:[number], y:[number]}
	* line:
	*    {
	*        first:{x:[number], y:[number]},
	*        second:{x:[number], y:[number]}
	*    }
	*/
	public static String classify(PVector p2, PVector p0, PVector p1) {
	    PVector a = new PVector(
	    	p1.x - p0.x,
	        p1.y - p0.y
	    );

	    PVector b = new PVector(
	        p2.x - p0.x,
	        p2.y - p0.y
	    );

	    float sa = a.x * b.y - b.x * a.y;
	    // if (sa > 0.0) return "left";
	    // if (sa < 0.0) return "right";
	    if ((a.x * b.x < 0.0) || (a.y * b.y < 0.0)) return "behind";
	    if (a.mag() < b.mag()) return "beyond";
	    // if (p0.equals(p2))return "origin";
	    // if (p1 == p2) return "destination";
	    return "between";
	}
}