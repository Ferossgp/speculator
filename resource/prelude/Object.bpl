// https://github.com/Microsoft/dafny/blob/master/Binaries/DafnyPrelude.bpl
// https://github.com/veriatl/VeriGT.CaseStudies/blob/master/Prelude/LibOCL.bpl
// https://github.com/Microsoft/vcc/blob/47f3f33d459f5fd9233203ec3d5d2fc8032b7db5/vcc/Headers/Vcc3Prelude.bpl

type ref;
type Field Object;

type Any;
type Integer = int;
type Number = int   ;

type Double = real;
type Sequence;
type Coll;

function Number#add(x: Number, y: Number, xs: Seq Number) returns (Number);
axiom (forall x, y: Number, xs: Seq Number :: Number#add(x, y, xs) == x + y + 0);
axiom (forall x, y: Number, xs: Seq Number :: Number#add(x, y, xs) == x + y +
      Number#add(Seq#Index(0), Seq#Index(1),Seq#Drop(xs, 2)));

function Number#minus(x: Number, y: Number) : Number { x - y};
function Number#multiply(x: Number, y: Number) : Number { x * y};
function Number#devide(x: Number, y: Number) : Number { x / y};
function Number#mod(x: Number, y: Number) : Number { x mod y};
function Number#div(x: Number, y: Number) : Number { x div y};

function max(x: Number, y: Number) returns (Number);
axiom (forall x: Number, y: Number :: { max(x, y) } x <= y <==> max(x,y) == y);
axiom (forall x: Number, y: Number :: { max(x, y) } y <= x <==> max(x,y) == x);
axiom (forall x: Number, y: Number :: { max(x, y) } max(x, y) == x || max(x, y) == y);

function min(x: Number, y: Number) returns (Number);
axiom (forall x: Number, y: Number :: { min(x, y) } x <= y <==> min(x,y) == x);
axiom (forall x: Number, y: Number :: { min(x, y) } y <= x <==> min(x,y) == y);
axiom (forall x: Number, y: Number :: { min(x, y) } min(x, y) == x || min(x, y) == y);

function inc(x: Number) returns (Number);
axiom (forall x: Number :: inc(x) == x + 1);

function dec(x: Number) returns (Number);
axiom (forall x: Number :: dec(x) == x - 1);

function quotient(x: Number, y: Number) returns (Number);
function remainder(x: Number, y: Number) returns (Number);

function isZero(x: Number) : bool {x == 0};
function isPos(x: Number) : bool{x > 0};
function isNeg(x: Number) : bool {x < 0};

function intCast(x: Any) returns (int);
function booleanCast(x: Any) returns (bool);
function longCast(x: Any) returns (Number);
function doubleCast(x: Any) returns (Double);

function count(x: Coll) returns (r: Integer);
axiom (forall x: Coll :: count(x) >= 0)

function nth<T>(x: Coll T) returns (T);

function lt(x: Number, y: Number) : bool { x < y};
function lte(x: Number, y: Number) : bool { x <= y};
function gt(x: Number, y: Number) : bool { x > y};
function gte(x: Number, y: Number) : bool { x >= y};

function equiv<T>(x: T, y: T) returns (bool);
function identical<T>(x: T, y: T) returns (bool);

Number#add(1, 1.0)
Double#add
