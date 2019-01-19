var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
/* Generated from Java with JSweet 2.0.0-SNAPSHOT - http://www.jsweet.org */
var bc19;
(function (bc19) {
    var Utils = (function () {
        function Utils(myRobot) {
            this.myRobot = null;
            this.robotMap = null;
            this.robotsInVision = null;
            this.dimY = 0;
            this.dimX = 0;
            this.myRobot = myRobot;
            this.dimY = myRobot.map.length;
            this.dimX = myRobot.map[0].length;
        }
        Utils.prototype.update = function () {
            this.robotMap = this.myRobot.getVisibleRobotMap();
            this.robotsInVision = this.myRobot.getVisibleRobots();
        };
        Utils.prototype.canBuild = function (unit) {
            if (this.myRobot.fuel < bc19.Constants.fuelCosts_$LI$()[unit])
                return false;
            if (this.myRobot.karbonite < bc19.Constants.karboCosts_$LI$()[unit])
                return false;
            return true;
        };
        Utils.prototype.canSafelyBuild = function (unit) {
            if (this.myRobot.fuel < bc19.Constants.fuelCosts_$LI$()[unit] + bc19.Constants.SAFETY_FUEL)
                return false;
            if (this.myRobot.karbonite < bc19.Constants.karboCosts_$LI$()[unit] + bc19.Constants.SAFETY_KARBO)
                return false;
            return true;
        };
        Utils.prototype.isInMap = function (x, y) {
            if (x < 0 || x >= this.dimX)
                return false;
            if (y < 0 || y >= this.dimY)
                return false;
            return true;
        };
        Utils.prototype.isEmptySpace = function (dx, dy) {
            return this.isEmptySpaceAbsolute(this.myRobot.me.x + dx, this.myRobot.me.y + dy);
        };
        Utils.prototype.isEmptySpaceAbsolute = function (x, y) {
            if (x < 0 || x >= this.dimX)
                return false;
            if (y < 0 || y >= this.dimY)
                return false;
            if (!this.myRobot.map[y][x])
                return false;
            return (this.robotMap[y][x] <= 0);
        };
        Utils.prototype.getRobot = function (x, y) {
            if (!this.isInMap(x, y))
                return null;
            if (this.robotMap[y][x] <= 0)
                return null;
            return this.myRobot.getRobot(this.robotMap[y][x]);
        };
        Utils.prototype.distance$int$int$int$int = function (x1, y1, x2, y2) {
            return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        };
        Utils.prototype.distance = function (x1, y1, x2, y2) {
            if (((typeof x1 === 'number') || x1 === null) && ((typeof y1 === 'number') || y1 === null) && ((typeof x2 === 'number') || x2 === null) && ((typeof y2 === 'number') || y2 === null)) {
                return this.distance$int$int$int$int(x1, y1, x2, y2);
            }
            else if (((x1 != null && x1 instanceof bc19.Location) || x1 === null) && ((y1 != null && y1 instanceof bc19.Location) || y1 === null) && x2 === undefined && y2 === undefined) {
                return this.distance$bc19_Location$bc19_Location(x1, y1);
            }
            else
                throw new Error('invalid overload');
        };
        Utils.prototype.distance$bc19_Location$bc19_Location = function (loc1, loc2) {
            return this.distance$int$int$int$int(loc1.x, loc1.y, loc2.x, loc2.y);
        };
        Utils.prototype.rich = function () {
            if (this.myRobot.karbonite < bc19.Constants.RICH_KARBO)
                return false;
            if (this.myRobot.fuel < bc19.Constants.RICH_FUEL)
                return false;
            return true;
        };
        Utils.prototype.superRich = function () {
            if (this.myRobot.karbonite < bc19.Constants.SUPER_RICH_KARBO)
                return false;
            if (this.myRobot.fuel < bc19.Constants.SUPER_RICH_FUEL)
                return false;
            return true;
        };
        return Utils;
    }());
    bc19.Utils = Utils;
    Utils["__class"] = "bc19.Utils";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Action = (function () {
        function Action(signal, signalRadius, logs, castleTalk) {
            this.signal = 0;
            this.signal_radius = 0;
            this.logs = null;
            this.castle_talk = 0;
            this.signal = signal;
            this.signal_radius = signalRadius;
            this.logs = logs;
            this.castle_talk = castleTalk;
        }
        return Action;
    }());
    bc19.Action = Action;
    Action["__class"] = "bc19.Action";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var DefenseMechanism = (function () {
        function DefenseMechanism(myRobot, utils) {
            this.firstSoldierIndex = 3;
            this.roundLastUnit = -100;
            this.myRobot = null;
            this.utils = null;
            this.myUnits = null;
            this.enemyUnits = null;
            this.micro = null;
            this.myRobot = myRobot;
            this.utils = utils;
            this.micro = new Array(bc19.Constants.UNITTYPES - this.firstSoldierIndex);
            for (var i = 0; i < this.micro.length; ++i) {
                this.micro[i] = new bc19.Micro(myRobot, utils, bc19.Constants.rad2Index, i + this.firstSoldierIndex);
            }
            ;
        }
        DefenseMechanism.prototype.defenseAction = function () {
            this.myUnits = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(bc19.Constants.UNITTYPES);
            this.enemyUnits = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(bc19.Constants.UNITTYPES);
            var totalUnits = 0;
            var totalEnemies = 0;
            var totalTroops = 0;
            var totalEnemyTroops = 0;
            for (var index121 = 0; index121 < this.utils.robotsInVision.length; index121++) {
                var r = this.utils.robotsInVision[index121];
                {
                    if (this.myRobot.isVisible(r)) {
                        if (r.team !== this.myRobot.me.team) {
                            ++this.enemyUnits[r.unit];
                            ++totalEnemies;
                            if (r.unit !== bc19.Constants.PILGRIM)
                                ++totalEnemyTroops;
                        }
                        else {
                            ++this.myUnits[r.unit];
                            ++totalUnits;
                            if (r.unit > bc19.Constants.PILGRIM)
                                ++totalTroops;
                        }
                    }
                }
            }
            if ((totalEnemies > 0 && totalEnemies === this.enemyUnits[bc19.Constants.PILGRIM] && totalTroops === 0) || totalEnemyTroops > 0) {
                return this.buildDefenseUnit();
            }
            return null;
        };
        DefenseMechanism.prototype.whichUnitToBuild = function () {
            return bc19.Constants.PROPHET;
        };
        DefenseMechanism.prototype.buildDefenseUnit = function () {
            var unitToBuild = this.whichUnitToBuild();
            if (!this.utils.canBuild(unitToBuild))
                return null;
            var index = this.micro[unitToBuild - this.firstSoldierIndex].getBestIndex();
            if (index == null)
                return null;
            return this.myRobot.buildUnit(unitToBuild, bc19.Constants.X_$LI$()[index], bc19.Constants.Y_$LI$()[index]);
        };
        DefenseMechanism.prototype.buildUnitRich = function () {
            if (this.utils.superRich()) {
                this.roundLastUnit = this.myRobot.me.turn;
                return this.buildDefenseUnit();
            }
            if (this.utils.rich() && this.roundLastUnit + bc19.Constants.MIN_TURNS_RICH <= this.myRobot.me.turn) {
                this.roundLastUnit = this.myRobot.me.turn;
                return this.buildDefenseUnit();
            }
            return null;
        };
        return DefenseMechanism;
    }());
    bc19.DefenseMechanism = DefenseMechanism;
    DefenseMechanism["__class"] = "bc19.DefenseMechanism";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var CastleUtils = (function () {
        function CastleUtils(myRobot) {
            this.nextTurnAction = null;
            this.allCastleLocs = false;
            this.castleIndex = 0;
            this.numCastles = 0;
            this.churchRequiredCont = 0;
            this.locBits = 64;
            this.myRobot = null;
            this.hSim = false;
            this.vSim = false;
            this.utils = null;
            this.myLocation = null;
            this.objectives = null;
            this.karbo = null;
            this.fuel = null;
            this.closestCastle = null;
            this.alive = null;
            this.initialTurn = 0;
            this.myCastles = null;
            this.XOccupied = null;
            this.YOccupied = null;
            this.targetByID = null;
            this.pilgrimInfoMap = null;
            this.defenseMechanism = null;
            this.myRobot = myRobot;
            this.initialTurn = myRobot.me.turn;
            this.hSim = this.vSim = true;
            this.utils = new bc19.Utils(myRobot);
            this.myLocation = new bc19.Location(myRobot.me.x, myRobot.me.y);
            this.objectives = null;
            this.defenseMechanism = new bc19.DefenseMechanism(myRobot, this.utils);
            this.initializePilgrimInfo();
            this.initializeCastleInfo();
        }
        CastleUtils.prototype.update = function () {
            this.utils.update();
            this.readCastleTalk();
            this.sendLocation();
            this.doFirstTurnsStuff();
            this.checkObjectives();
            this.nextTurnAction = null;
        };
        CastleUtils.prototype.checkDefense = function () {
            var act = this.defenseMechanism.defenseAction();
            if (act != null) {
                this.nextTurnAction = act;
            }
        };
        CastleUtils.prototype.checkFreeBuild = function () {
            if (this.nextTurnAction != null)
                return;
            this.nextTurnAction = this.defenseMechanism.buildUnitRich();
        };
        CastleUtils.prototype.doFirstTurnsStuff = function () {
            if (this.myRobot.me.turn === this.initialTurn) {
                this.getCastleIndex();
                this.sendIndex();
            }
            else if (this.myRobot.me.turn === this.initialTurn + 1)
                this.myCastles = this.myCastles.slice(0, this.numCastles);
        };
        CastleUtils.prototype.createPilgrim = function (objectiveIndex) {
            var dir = this.objectives[objectiveIndex].dir;
            if (!this.utils.canBuild(bc19.Constants.PILGRIM))
                return false;
            for (var i = 0; i < bc19.Constants.rad2Index; ++i) {
                if (this.utils.isEmptySpace(bc19.Constants.X_$LI$()[dir], bc19.Constants.Y_$LI$()[dir])) {
                    this.nextTurnAction = this.myRobot.buildUnit(bc19.Constants.PILGRIM, bc19.Constants.X_$LI$()[dir], bc19.Constants.Y_$LI$()[dir]);
                    this.sendMiningLocation(objectiveIndex, dir);
                    return true;
                }
                ++dir;
                if (dir >= bc19.Constants.rad2Index)
                    dir = 0;
            }
            ;
            return false;
        };
        CastleUtils.prototype.sendMiningLocation = function (objectiveIndex, dir) {
            var rad = bc19.Constants.Steplength_$LI$()[dir];
            var message = ((this.objectives[objectiveIndex].x * bc19.Constants.maxMapSize) + this.objectives[objectiveIndex].y);
            this.signalWithRound(message, rad);
        };
        CastleUtils.prototype.signalWithRound = function (message, rad) {
            message = (((this.myRobot.me.turn) % 2) * bc19.Constants.LocationSize_$LI$() + message);
            this.myRobot.signal(message, rad);
        };
        CastleUtils.prototype.generateObjectiveList = function () {
            var dist = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var dir = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var fuelM = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var closestCastle = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var queue = ([]);
            for (var i = 0; i < this.myCastles.length; ++i) {
                if (this.myCastles[i].wellDefined()) {
                    dist[this.myCastles[i].x][this.myCastles[i].y] = 1;
                    closestCastle[this.myCastles[i].x][this.myCastles[i].y] = i;
                    /* add */ (queue.push(new bc19.Location(this.myCastles[i].x, this.myCastles[i].y)) > 0);
                }
            }
            ;
            var aux = ([]);
            var karboCont = 0;
            var fuelCont = 0;
            while ((!(queue.length == 0))) {
                var last = (function (a) { return a.length == 0 ? null : a.shift(); })(queue);
                var lastDist = dist[last.x][last.y];
                var castle = closestCastle[last.x][last.y];
                if (this.myRobot.fuelMap[last.y][last.x]) {
                    /* add */ (aux.push(new CastleUtils.Objective(this, bc19.Constants.OBJ_FUEL, lastDist - 1, last.x, last.y, dir[last.x][last.y], castle)) > 0);
                    ++fuelCont;
                }
                if (this.myRobot.karboniteMap[last.y][last.x]) {
                    /* add */ (aux.push(new CastleUtils.Objective(this, bc19.Constants.OBJ_KARBO, lastDist - 1, last.x, last.y, dir[last.x][last.y], castle)) > 0);
                    ++karboCont;
                }
                var limit = bc19.Constants.rad4Index;
                if (lastDist === 1)
                    limit = bc19.Constants.rad2Index;
                for (var i = 0; i < limit; ++i) {
                    var newX = last.x + bc19.Constants.X_$LI$()[i];
                    var newY = last.y + bc19.Constants.Y_$LI$()[i];
                    var newFuel = fuelM[last.x][last.y] + bc19.Constants.Steplength_$LI$()[i];
                    if (this.utils.isInMap(newX, newY) && this.utils.isEmptySpaceAbsolute(newX, newY)) {
                        if (dist[newX][newY] === 0) {
                            /* add */ (queue.push(new bc19.Location(newX, newY)) > 0);
                            dist[newX][newY] = lastDist + 1;
                            fuelM[newX][newY] = newFuel;
                            closestCastle[newX][newY] = castle;
                            if (lastDist === 1)
                                dir[newX][newY] = i;
                            else
                                dir[newX][newY] = dir[last.x][last.y];
                        }
                        if (dist[newX][newY] === lastDist + 1) {
                            if (fuelM[newX][newY] > newFuel) {
                                fuelM[newX][newY] = newFuel;
                                closestCastle[newX][newY] = castle;
                                if (lastDist === 1)
                                    dir[newX][newY] = i;
                                else
                                    dir[newX][newY] = dir[last.x][last.y];
                            }
                        }
                    }
                }
                ;
            }
            ;
            this.objectives = aux.slice(0);
            this.karbo = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(karboCont);
            this.fuel = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(fuelCont);
            var indKarbo = 0;
            var indFuel = 0;
            for (var i = 0; i < this.objectives.length; ++i) {
                if (this.objectives[i].type === bc19.Constants.OBJ_KARBO)
                    this.karbo[indKarbo++] = i;
                if (this.objectives[i].type === bc19.Constants.OBJ_FUEL)
                    this.fuel[indFuel++] = i;
            }
            ;
        };
        CastleUtils.prototype.shouldBuildPilgrim = function () {
            if (this.churchRequiredCont > bc19.Constants.MAX_CHURCHES_WAITING)
                this.churchRequiredCont = bc19.Constants.MAX_CHURCHES_WAITING;
            var totalCostKarbo = bc19.Constants.SAFETY_KARBO + this.churchRequiredCont * bc19.Constants.karboCosts_$LI$()[bc19.Constants.CHURCH];
            var totalCostFuel = bc19.Constants.SAFETY_FUEL + this.churchRequiredCont * bc19.Constants.fuelCosts_$LI$()[bc19.Constants.CHURCH];
            if (this.objectives == null)
                return null;
            var ans = null;
            for (var objective = 0; objective < this.objectives.length; ++objective) {
                var occ = this.isOccupied(objective);
                if (occ === bc19.Constants.FREE) {
                    totalCostKarbo += bc19.Constants.karboCosts_$LI$()[bc19.Constants.PILGRIM];
                    totalCostFuel += bc19.Constants.fuelCosts_$LI$()[bc19.Constants.PILGRIM];
                    if (this.objectives[objective].castle === this.castleIndex) {
                        ans = objective;
                        break;
                    }
                }
                else if (occ === bc19.Constants.PARTIALLLY_OCCUPIED) {
                    if (this.objectives[objective].castle === this.castleIndex)
                        return null;
                    else {
                        totalCostKarbo += bc19.Constants.karboCosts_$LI$()[bc19.Constants.PILGRIM];
                        totalCostFuel += bc19.Constants.fuelCosts_$LI$()[bc19.Constants.PILGRIM];
                    }
                }
            }
            ;
            if (ans == null)
                return null;
            if (totalCostKarbo > this.myRobot.karbonite)
                return null;
            if (totalCostFuel > this.myRobot.fuel)
                return null;
            return ans;
        };
        CastleUtils.prototype.initializePilgrimInfo = function () {
            this.pilgrimInfoMap = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return undefined;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            this.targetByID = new Array(bc19.Constants.MAX_ID);
        };
        CastleUtils.prototype.readCastleTalk = function () {
            this.XOccupied = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(this.utils.dimX);
            this.YOccupied = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(this.utils.dimY);
            this.churchRequiredCont = 0;
            for (var index122 = 0; index122 < this.utils.robotsInVision.length; index122++) {
                var r = this.utils.robotsInVision[index122];
                {
                    if (r.team === this.myRobot.me.team && r.castle_talk > 0) {
                        var mes = ((r.castle_talk - 1) / bc19.Constants.maxMapSize | 0);
                        if (mes === bc19.Constants.MSG_PILGRIM || mes === bc19.Constants.MSG_CHURCH) {
                            if (mes === bc19.Constants.MSG_CHURCH)
                                ++this.churchRequiredCont;
                            var locInfo = (r.castle_talk - 1) % bc19.Constants.maxMapSize;
                            this.updatePilgrimInfo(r.id, locInfo, this.myRobot.me.turn % 2);
                        }
                        if (mes === bc19.Constants.MSG_CASTLE) {
                            if (this.myRobot.me.turn === this.initialTurn + 1)
                                ++this.numCastles;
                            var locInfo = (r.castle_talk - 1) % bc19.Constants.maxMapSize;
                            this.updateCastleInfo(r.id, locInfo, this.myRobot.me.turn % 2);
                        }
                    }
                }
            }
        };
        CastleUtils.prototype.updateCastleInfo = function (id, partialLoc, turn) {
            if (id === this.myRobot.me.id)
                return;
            var found = false;
            for (var i = 0; i < this.myCastles.length; ++i) {
                if (this.myCastles[i].id === id) {
                    found = true;
                    if (i > this.castleIndex)
                        turn = (turn + 1) % 2;
                    if (turn === 0)
                        this.myCastles[i].x = partialLoc;
                    else
                        this.myCastles[i].y = partialLoc;
                    this.myCastles[i].lastReportedTurn = this.myRobot.me.turn;
                }
            }
            ;
            if (!found) {
                this.myCastles[partialLoc].id = id;
                this.myCastles[partialLoc].lastReportedTurn = this.myRobot.me.turn;
            }
        };
        CastleUtils.prototype.updatePilgrimInfo = function (id, partialLoc, turn) {
            var loc = this.targetByID[id];
            if (loc == null)
                loc = new bc19.Location(-1, -1);
            if (turn === 0) {
                if (loc.x === -1) {
                    loc.x = partialLoc;
                    if (loc.y === -1)
                        ++this.XOccupied[loc.x];
                }
                else if (loc.x !== partialLoc) {
                    if (loc.y !== -1)
                        this.pilgrimInfoMap[loc.x][loc.y] = null;
                    loc.x = partialLoc;
                    loc.y = -1;
                    ++this.XOccupied[loc.x];
                }
            }
            else {
                if (loc.y === -1) {
                    loc.y = partialLoc;
                    if (loc.x === -1)
                        ++this.YOccupied[loc.y];
                }
                else if (loc.y !== partialLoc) {
                    if (loc.x !== -1)
                        this.pilgrimInfoMap[loc.x][loc.y] = null;
                    loc.y = partialLoc;
                    loc.x = -1;
                    ++this.YOccupied[loc.y];
                }
            }
            this.targetByID[id] = loc;
            if (loc.x !== -1 && loc.y !== -1) {
                this.pilgrimInfoMap[loc.x][loc.y] = new CastleUtils.PilgrimInfo(this, id, this.myRobot.me.turn);
            }
        };
        CastleUtils.prototype.isOccupied = function (objectiveIndex) {
            var obj = this.objectives[objectiveIndex];
            if (this.XOccupied[obj.x] > 0 || this.YOccupied[obj.y] > 0)
                return bc19.Constants.PARTIALLLY_OCCUPIED;
            var pi = this.pilgrimInfoMap[obj.x][obj.y];
            if (pi != null && pi.lastTurnReporting + bc19.Constants.MAX_TURNS_REPORTING > this.myRobot.me.turn)
                return bc19.Constants.OCCUPIED;
            return bc19.Constants.FREE;
        };
        CastleUtils.prototype.printObjectives = function () {
            for (var i = 0; i < this.utils.dimX; ++i) {
                for (var j = 0; j < this.utils.dimY; ++j) {
                    var pi = this.pilgrimInfoMap[i][j];
                    if (pi != null)
                        this.myRobot.log("Robot with id " + pi.id + " is going to " + i + " " + j);
                }
                ;
            }
            ;
        };
        CastleUtils.prototype.getCastleIndex = function () {
            for (var index123 = 0; index123 < this.utils.robotsInVision.length; index123++) {
                var r = this.utils.robotsInVision[index123];
                {
                    if (r.team === this.myRobot.me.team && r.castle_talk > 0)
                        ++this.castleIndex;
                }
            }
            this.myCastles[this.castleIndex].x = this.myLocation.x;
            this.myCastles[this.castleIndex].y = this.myLocation.y;
            this.myCastles[this.castleIndex].id = this.myRobot.me.id;
        };
        CastleUtils.prototype.sendLocation = function () {
            var mes = (bc19.Constants.MSG_CASTLE) * this.locBits;
            if (this.myRobot.me.turn % 2 === 0)
                mes += this.myLocation.x;
            else
                mes += this.myLocation.y;
            this.myRobot.castleTalk(mes + 1);
        };
        CastleUtils.prototype.sendIndex = function () {
            var mes = (bc19.Constants.MSG_CASTLE) * this.locBits + this.castleIndex;
            this.myRobot.castleTalk(mes + 1);
        };
        CastleUtils.prototype.initializeCastleInfo = function () {
            this.myCastles = new Array(bc19.Constants.MAX_CASTLES);
            for (var i = 0; i < this.myCastles.length; ++i)
                this.myCastles[i] = new CastleUtils.CastleInfo(this);
        };
        CastleUtils.prototype.checkObjectives = function () {
            if (!this.allCastleLocs) {
                var aux = true;
                for (var i = 0; i < this.myCastles.length; ++i)
                    if (!this.myCastles[i].wellDefined())
                        aux = false;
                ;
                if (aux) {
                    this.allCastleLocs = true;
                    this.generateObjectiveList();
                }
            }
            else {
                var shouldCheck = false;
                for (var i = 0; i < this.myCastles.length; ++i) {
                    if (i !== this.castleIndex && this.myCastles[i].alive && !this.myCastles[i].isAlive()) {
                        this.myCastles[i].alive = false;
                        shouldCheck = true;
                    }
                }
                ;
                if (shouldCheck)
                    this.generateObjectiveList();
            }
            if (this.objectives == null)
                this.generateObjectiveList();
        };
        CastleUtils.prototype.printCastleInfo = function () {
            this.myRobot.log("Total number of castles " + this.numCastles);
            this.myRobot.log("My index is " + this.castleIndex);
            for (var i = 0; i < this.myCastles.length; ++i) {
                this.myRobot.log("Castle " + i);
                this.myRobot.log(this.myCastles[i].id + " " + this.myCastles[i].x + " " + this.myCastles[i].y);
            }
            ;
        };
        return CastleUtils;
    }());
    bc19.CastleUtils = CastleUtils;
    CastleUtils["__class"] = "bc19.CastleUtils";
    (function (CastleUtils) {
        var Objective = (function () {
            function Objective(__parent, type, dist, x, y, dir, castle) {
                this.__parent = __parent;
                this.type = 0;
                this.dist = 0;
                this.x = 0;
                this.y = 0;
                this.dir = 0;
                this.castle = 0;
                this.type = type;
                this.dist = dist;
                this.x = x;
                this.y = y;
                this.dir = dir;
                this.castle = castle;
            }
            Objective.prototype.print = function () {
                this.__parent.myRobot.log(this.type + " " + this.dist + " " + this.x + " " + this.y + " " + this.dir + " " + this.castle);
            };
            return Objective;
        }());
        CastleUtils.Objective = Objective;
        Objective["__class"] = "bc19.CastleUtils.Objective";
        var PilgrimInfo = (function () {
            function PilgrimInfo(__parent, id, lastTurnReporting) {
                this.__parent = __parent;
                this.id = 0;
                this.lastTurnReporting = 0;
                this.id = id;
                this.lastTurnReporting = lastTurnReporting;
            }
            return PilgrimInfo;
        }());
        CastleUtils.PilgrimInfo = PilgrimInfo;
        PilgrimInfo["__class"] = "bc19.CastleUtils.PilgrimInfo";
        var CastleInfo = (function () {
            function CastleInfo(__parent) {
                this.__parent = __parent;
                this.id = 0;
                this.x = 0;
                this.y = 0;
                this.lastReportedTurn = 0;
                this.alive = true;
                this.id = -1;
                this.x = -1;
                this.y = -1;
                this.lastReportedTurn = 0;
                __parent.alive = true;
            }
            CastleInfo.prototype.wellDefined = function () {
                return this.id >= 0 && (function (lhs, rhs) { return lhs && rhs; })(this.x >= 0, this.y >= 0);
            };
            CastleInfo.prototype.isAlive = function () {
                return this.__parent.myRobot.me.turn - this.lastReportedTurn < bc19.Constants.MAX_TURNS_REPORTING;
            };
            return CastleInfo;
        }());
        CastleUtils.CastleInfo = CastleInfo;
        CastleInfo["__class"] = "bc19.CastleUtils.CastleInfo";
    })(CastleUtils = bc19.CastleUtils || (bc19.CastleUtils = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var BCException = (function (_super) {
        __extends(BCException, _super);
        function BCException(errorMessage) {
            var _this = _super.call(this, errorMessage) || this;
            _this.message = errorMessage;
            Object.setPrototypeOf(_this, BCException.prototype);
            return _this;
        }
        return BCException;
    }(Error));
    bc19.BCException = BCException;
    BCException["__class"] = "bc19.BCException";
    BCException["__interfaces"] = ["java.io.Serializable"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var ChurchBuild = (function () {
        function ChurchBuild(myRobot, pilgrim) {
            this.builtChurch = false;
            this.myRobot = null;
            this.pilgrim = null;
            this.utils = null;
            this.buildValue = null;
            this.maxValue = 0;
            this.churchBuildInfo = null;
            this.myRobot = myRobot;
            this.pilgrim = pilgrim;
            this.utils = pilgrim.utils;
        }
        ChurchBuild.prototype.tryBuildChurch = function () {
            this.builtChurch = false;
            if (!this.pilgrim.onObjective())
                return;
            if (!this.pilgrim.needChurch())
                return;
            if (!this.utils.canSafelyBuild(bc19.Constants.CHURCH))
                return;
            this.maxValue = 0;
            this.churchBuildInfo = new Array(bc19.Constants.rad2Index);
            for (var i = 0; i < this.churchBuildInfo.length; ++i) {
                this.churchBuildInfo[i] = new ChurchBuild.ChurchBuildInfo(this, i, this.getValue$int(i));
                if (this.maxValue < this.churchBuildInfo[i].value)
                    this.maxValue = this.churchBuildInfo[i].value;
            }
            ;
            var bestLoc = null;
            for (var i = 0; i < this.churchBuildInfo.length; ++i) {
                if (this.churchBuildInfo[i].value === this.maxValue && this.churchBuildInfo[i].value > 0)
                    this.churchBuildInfo[i].computeParameters();
                if (this.churchBuildInfo[i].isBetter(bestLoc))
                    bestLoc = this.churchBuildInfo[i];
            }
            ;
            if (bestLoc != null) {
                this.builtChurch = true;
                this.pilgrim.actionToPerform = this.myRobot.buildUnit(bc19.Constants.CHURCH, bestLoc.dx, bestLoc.dy);
            }
        };
        ChurchBuild.prototype.getValue$int$int = function (x, y) {
            if (!this.utils.isEmptySpaceAbsolute(x, y))
                return 0;
            if (this.myRobot.karboniteMap[y][x])
                return 1;
            if (this.myRobot.fuelMap[y][x])
                return 1;
            return 2;
        };
        ChurchBuild.prototype.getValue = function (x, y) {
            if (((typeof x === 'number') || x === null) && ((typeof y === 'number') || y === null)) {
                return this.getValue$int$int(x, y);
            }
            else if (((typeof x === 'number') || x === null) && y === undefined) {
                return this.getValue$int(x);
            }
            else
                throw new Error('invalid overload');
        };
        ChurchBuild.prototype.getValue$int = function (i) {
            return this.getValue$int$int(this.myRobot.me.x + bc19.Constants.X_$LI$()[i], this.myRobot.me.y + bc19.Constants.Y_$LI$()[i]);
        };
        ChurchBuild.prototype.isBetter = function (i, j) {
            var newX1 = this.myRobot.me.x + bc19.Constants.X_$LI$()[i];
            var newY1 = this.myRobot.me.y + bc19.Constants.Y_$LI$()[i];
            var newX2 = this.myRobot.me.x + bc19.Constants.X_$LI$()[j];
            var newY2 = this.myRobot.me.y + bc19.Constants.Y_$LI$()[j];
            if (!this.utils.isEmptySpaceAbsolute(newX2, newY2))
                return false;
            if (i === -1)
                return true;
            if (!this.utils.isEmptySpaceAbsolute(newX1, newY1))
                return true;
            if (this.myRobot.karboniteMap[newY1][newX1])
                return true;
            if (this.myRobot.fuelMap[newY1][newX1])
                return true;
            return false;
        };
        return ChurchBuild;
    }());
    bc19.ChurchBuild = ChurchBuild;
    ChurchBuild["__class"] = "bc19.ChurchBuild";
    (function (ChurchBuild) {
        var ChurchBuildInfo = (function () {
            function ChurchBuildInfo(__parent, index, value) {
                this.MAX_DIST = 5;
                this.__parent = __parent;
                this.resourcesNearby = 0;
                this.totalFuel = 0;
                this.dx = 0;
                this.dy = 0;
                this.value = 0;
                this.resourcesNearby = 0;
                this.totalFuel = 0;
                this.dx = bc19.Constants.X_$LI$()[index];
                this.dy = bc19.Constants.Y_$LI$()[index];
                this.value = value;
            }
            ChurchBuildInfo.prototype.isAdjacent = function (dx, dy) {
                if (Math.abs(dx - this.dx) > 1)
                    return false;
                if (Math.abs(dy - this.dy) > 1)
                    return false;
                return true;
            };
            ChurchBuildInfo.prototype.isBetter = function (c) {
                if (this.value === 0)
                    return false;
                if (c == null)
                    return true;
                if (this.value > c.value)
                    return true;
                if (c.value > this.value)
                    return false;
                if (this.resourcesNearby > c.resourcesNearby)
                    return true;
                if (c.resourcesNearby > this.resourcesNearby)
                    return false;
                return this.totalFuel <= c.totalFuel;
            };
            ChurchBuildInfo.prototype.computeParameters = function () {
                var distM = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                    return 0;
                }
                else {
                    var array = [];
                    for (var i = 0; i < dims[0]; i++) {
                        array.push(allocate(dims.slice(1)));
                    }
                    return array;
                } }; return allocate(dims); })([2 * this.MAX_DIST + 1, 2 * this.MAX_DIST + 1]);
                var fuelM = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                    return 0;
                }
                else {
                    var array = [];
                    for (var i = 0; i < dims[0]; i++) {
                        array.push(allocate(dims.slice(1)));
                    }
                    return array;
                } }; return allocate(dims); })([2 * this.MAX_DIST + 1, 2 * this.MAX_DIST + 1]);
                var queue = ([]);
                for (var i = 0; i < bc19.Constants.rad2Index; ++i) {
                    var x = bc19.Constants.X_$LI$()[i];
                    var y = bc19.Constants.Y_$LI$()[i];
                    if (this.__parent.utils.isInMap(this.__parent.myRobot.me.x + this.dx + x, this.__parent.myRobot.me.y + this.dy + y)) {
                        /* add */ (queue.push(new bc19.Location(x, y)) > 0);
                        distM[x + this.MAX_DIST][y + this.MAX_DIST] = 1;
                    }
                }
                ;
                while ((!(queue.length == 0))) {
                    var last = (function (a) { return a.length == 0 ? null : a.shift(); })(queue);
                    var lastX = last.x + this.MAX_DIST;
                    var lastY = last.y + this.MAX_DIST;
                    var lastFuel = fuelM[lastX][lastY];
                    var lastDist = distM[lastX][lastY];
                    var lastActualX = this.__parent.myRobot.me.x + this.dx + last.x;
                    var lastActualY = this.__parent.myRobot.me.y + this.dy + last.y;
                    if (this.__parent.myRobot.karboniteMap[lastActualY][lastActualX] || this.__parent.myRobot.fuelMap[lastActualY][lastActualX]) {
                        ++this.resourcesNearby;
                        this.totalFuel += lastFuel;
                    }
                    else {
                        for (var i = 0; i < bc19.Constants.rad4Index; ++i) {
                            var newFuel = lastFuel + 2 * (bc19.Constants.Steplength_$LI$()[i] + bc19.Constants.FUEL_MINING_RATE);
                            if (newFuel > bc19.Constants.MAX_FUEL_LOST)
                                continue;
                            var newX = last.x + bc19.Constants.X_$LI$()[i];
                            var newY = last.y + bc19.Constants.Y_$LI$()[i];
                            var actualX = this.__parent.myRobot.me.x + this.dx + newX;
                            var actualY = this.__parent.myRobot.me.y + this.dy + newY;
                            if (this.in(newX, newY) && this.__parent.utils.isInMap(actualX, actualY) && this.__parent.myRobot.map[actualY][actualX]) {
                                newX += this.MAX_DIST;
                                newY += this.MAX_DIST;
                                if (distM[newX][newY] === 0) {
                                    /* add */ (queue.push(new bc19.Location(newX - this.MAX_DIST, newY - this.MAX_DIST)) > 0);
                                    distM[newX][newY] = lastDist + 1;
                                    fuelM[newX][newY] = newFuel;
                                }
                                if (distM[newX][newY] === lastDist + 1) {
                                    if (fuelM[newX][newY] > newFuel) {
                                        fuelM[newX][newY] = newFuel;
                                    }
                                }
                            }
                        }
                        ;
                    }
                }
                ;
            };
            ChurchBuildInfo.prototype.print = function () {
                this.__parent.myRobot.log("Church at location " + (this.__parent.myRobot.me.x + this.dx) + " " + (this.__parent.myRobot.me.y + this.dy) + " " + this.resourcesNearby + " " + this.totalFuel);
            };
            ChurchBuildInfo.prototype.in = function (x, y) {
                if (x < -this.MAX_DIST)
                    return false;
                if (x > this.MAX_DIST)
                    return false;
                if (y < -this.MAX_DIST)
                    return false;
                if (y > this.MAX_DIST)
                    return false;
                return true;
            };
            return ChurchBuildInfo;
        }());
        ChurchBuild.ChurchBuildInfo = ChurchBuildInfo;
        ChurchBuildInfo["__class"] = "bc19.ChurchBuild.ChurchBuildInfo";
    })(ChurchBuild = bc19.ChurchBuild || (bc19.ChurchBuild = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Location = (function () {
        function Location(x, y) {
            this.x = 0;
            this.y = 0;
            this.x = x;
            this.y = y;
        }
        Location.prototype.add$int = function (i) {
            return new Location(this.x + bc19.Constants.X_$LI$()[i], this.y + bc19.Constants.Y_$LI$()[i]);
        };
        Location.prototype.add$int$int = function (dx, dy) {
            return new Location(this.x + dx, this.y + dy);
        };
        Location.prototype.add = function (dx, dy) {
            if (((typeof dx === 'number') || dx === null) && ((typeof dy === 'number') || dy === null)) {
                return this.add$int$int(dx, dy);
            }
            else if (((typeof dx === 'number') || dx === null) && dy === undefined) {
                return this.add$int(dx);
            }
            else
                throw new Error('invalid overload');
        };
        return Location;
    }());
    bc19.Location = Location;
    Location["__class"] = "bc19.Location";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var BCAbstractRobot = (function () {
        function BCAbstractRobot() {
            this.SPECS = null;
            this.gameState = null;
            this.logs = null;
            this.__signal = 0;
            this.signalRadius = 0;
            this.__castleTalk = 0;
            this.me = null;
            this.id = 0;
            this.fuel = 0;
            this.karbonite = 0;
            this.lastOffer = null;
            this.map = null;
            this.karboniteMap = null;
            this.fuelMap = null;
            this.resetState();
        }
        BCAbstractRobot.prototype.setSpecs = function (specs) {
            this.SPECS = specs;
        };
        /*private*/ BCAbstractRobot.prototype.resetState = function () {
            this.logs = ([]);
            this.__signal = 0;
            this.signalRadius = 0;
            this.__castleTalk = 0;
        };
        BCAbstractRobot.prototype._do_turn = function (gameState) {
            this.gameState = gameState;
            this.id = gameState.id;
            this.karbonite = gameState.karbonite;
            this.fuel = gameState.fuel;
            this.lastOffer = gameState.last_offer;
            this.me = this.getRobot(this.id);
            if (this.me.turn === 1) {
                this.map = gameState.map;
                this.karboniteMap = gameState.karbonite_map;
                this.fuelMap = gameState.fuel_map;
            }
            var t = null;
            try {
                t = this.turn();
            }
            catch (e) {
                t = new bc19.ErrorAction(e, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
            }
            ;
            if (t == null)
                t = new bc19.Action(this.__signal, this.signalRadius, this.logs, this.__castleTalk);
            t.signal = this.__signal;
            t.signal_radius = this.signalRadius;
            t.logs = this.logs;
            t.castle_talk = this.__castleTalk;
            this.resetState();
            return t;
        };
        /*private*/ BCAbstractRobot.prototype.checkOnMap = function (x, y) {
            return x >= 0 && x < this.gameState.shadow[0].length && y >= 0 && y < this.gameState.shadow.length;
        };
        BCAbstractRobot.prototype.log = function (message) {
            /* add */ (this.logs.push(message) > 0);
        };
        BCAbstractRobot.prototype.signal = function (value, radius) {
            if (this.fuel < radius)
                throw new bc19.BCException("Not enough fuel to signal given radius.");
            if (value < 0 || value >= Math.pow(2, this.SPECS.COMMUNICATION_BITS))
                throw new bc19.BCException("Invalid signal, must be within bit range.");
            if (radius > 2 * Math.pow(this.SPECS.MAX_BOARD_SIZE - 1, 2))
                throw new bc19.BCException("Signal radius is too big.");
            this.__signal = value;
            this.signalRadius = radius;
            this.fuel -= radius;
        };
        BCAbstractRobot.prototype.castleTalk = function (value) {
            if (value < 0 || value >= Math.pow(2, this.SPECS.CASTLE_TALK_BITS))
                throw new bc19.BCException("Invalid castle talk, must be between 0 and 2^8.");
            this.__castleTalk = value;
        };
        BCAbstractRobot.prototype.proposeTrade = function (k, f) {
            if (this.me.unit !== this.SPECS.CASTLE)
                throw new bc19.BCException("Only castles can trade.");
            if (Math.abs(k) >= this.SPECS.MAX_TRADE || Math.abs(f) >= this.SPECS.MAX_TRADE)
                throw new bc19.BCException("Cannot trade over " + ('' + (this.SPECS.MAX_TRADE)) + " in a given turn.");
            return new bc19.TradeAction(f, k, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.buildUnit = function (unit, dx, dy) {
            if (this.me.unit !== this.SPECS.PILGRIM && this.me.unit !== this.SPECS.CASTLE && this.me.unit !== this.SPECS.CHURCH)
                throw new bc19.BCException("This unit type cannot build.");
            if (this.me.unit === this.SPECS.PILGRIM && unit !== this.SPECS.CHURCH)
                throw new bc19.BCException("Pilgrims can only build churches.");
            if (this.me.unit !== this.SPECS.PILGRIM && unit === this.SPECS.CHURCH)
                throw new bc19.BCException("Only pilgrims can build churches.");
            if (dx < -1 || dy < -1 || dx > 1 || dy > 1)
                throw new bc19.BCException("Can only build in adjacent squares.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t build units off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] !== 0)
                throw new bc19.BCException("Cannot build on occupied tile.");
            if (!this.map[this.me.y + dy][this.me.x + dx])
                throw new bc19.BCException("Cannot build onto impassable terrain.");
            if (this.karbonite < this.SPECS.UNITS[unit].CONSTRUCTION_KARBONITE || this.fuel < this.SPECS.UNITS[unit].CONSTRUCTION_FUEL)
                throw new bc19.BCException("Cannot afford to build specified unit.");
            return new bc19.BuildAction(unit, dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.move = function (dx, dy) {
            if (this.me.unit === this.SPECS.CASTLE || this.me.unit === this.SPECS.CHURCH)
                throw new bc19.BCException("Churches and Castles cannot move.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t move off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] === -1)
                throw new bc19.BCException("Cannot move outside of vision range.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] !== 0)
                throw new bc19.BCException("Cannot move onto occupied tile.");
            if (!this.map[this.me.y + dy][this.me.x + dx])
                throw new bc19.BCException("Cannot move onto impassable terrain.");
            var r = dx * dx + dy * dy;
            if (r > this.SPECS.UNITS[this.me.unit].SPEED)
                throw new bc19.BCException("Slow down, cowboy.  Tried to move faster than unit can.");
            if (this.fuel < r * this.SPECS.UNITS[this.me.unit].FUEL_PER_MOVE)
                throw new bc19.BCException("Not enough fuel to move at given speed.");
            return new bc19.MoveAction(dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.mine = function () {
            if (this.me.unit !== this.SPECS.PILGRIM)
                throw new bc19.BCException("Only Pilgrims can mine.");
            if (this.fuel < this.SPECS.MINE_FUEL_COST)
                throw new bc19.BCException("Not enough fuel to mine.");
            if (this.karboniteMap[this.me.y][this.me.x]) {
                if (this.me.karbonite >= this.SPECS.UNITS[this.SPECS.PILGRIM].KARBONITE_CAPACITY)
                    throw new bc19.BCException("Cannot mine, as at karbonite capacity.");
            }
            else if (this.fuelMap[this.me.y][this.me.x]) {
                if (this.me.fuel >= this.SPECS.UNITS[this.SPECS.PILGRIM].FUEL_CAPACITY)
                    throw new bc19.BCException("Cannot mine, as at fuel capacity.");
            }
            else
                throw new bc19.BCException("Cannot mine square without fuel or karbonite.");
            return new bc19.MineAction(this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.give = function (dx, dy, k, f) {
            if (dx > 1 || dx < -1 || dy > 1 || dy < -1 || (dx === 0 && dy === 0))
                throw new bc19.BCException("Can only give to adjacent squares.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t give off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] <= 0)
                throw new bc19.BCException("Cannot give to empty square.");
            if (k < 0 || f < 0 || this.me.karbonite < k || this.me.fuel < f)
                throw new bc19.BCException("Do not have specified amount to give.");
            return new bc19.GiveAction(k, f, dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.attack = function (dx, dy) {
            if (this.me.unit !== this.SPECS.CRUSADER && this.me.unit !== this.SPECS.PREACHER && this.me.unit !== this.SPECS.PROPHET)
                throw new bc19.BCException("Given unit cannot attack.");
            if (this.fuel < this.SPECS.UNITS[this.me.unit].ATTACK_FUEL_COST)
                throw new bc19.BCException("Not enough fuel to attack.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t attack off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] === -1)
                throw new bc19.BCException("Cannot attack outside of vision range.");
            if (!this.map[this.me.y + dy][this.me.x + dx])
                throw new bc19.BCException("Cannot attack impassable terrain.");
            var r = dx * dx + dy * dy;
            if (r > this.SPECS.UNITS[this.me.unit].ATTACK_RADIUS[1] || r < this.SPECS.UNITS[this.me.unit].ATTACK_RADIUS[0])
                throw new bc19.BCException("Cannot attack outside of attack range.");
            return new bc19.AttackAction(dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.getRobot = function (id) {
            if (id <= 0)
                return null;
            for (var i = 0; i < this.gameState.visible.length; i++) {
                if (this.gameState.visible[i].id === id) {
                    return this.gameState.visible[i];
                }
            }
            ;
            return null;
        };
        BCAbstractRobot.prototype.isVisible = function (robot) {
            for (var x = 0; x < this.gameState.shadow[0].length; x++) {
                for (var y = 0; y < this.gameState.shadow.length; y++) {
                    if (robot.id === this.gameState.shadow[y][x])
                        return true;
                }
                ;
            }
            ;
            return false;
        };
        BCAbstractRobot.prototype.isRadioing = function (robot) {
            return robot.signal >= 0;
        };
        BCAbstractRobot.prototype.getVisibleRobotMap = function () {
            return this.gameState.shadow;
        };
        BCAbstractRobot.prototype.getPassableMap = function () {
            return this.map;
        };
        BCAbstractRobot.prototype.getKarboniteMap = function () {
            return this.karboniteMap;
        };
        BCAbstractRobot.prototype.getFuelMap = function () {
            return this.fuelMap;
        };
        BCAbstractRobot.prototype.getVisibleRobots = function () {
            return this.gameState.visible;
        };
        BCAbstractRobot.prototype.turn = function () {
            return null;
        };
        return BCAbstractRobot;
    }());
    bc19.BCAbstractRobot = BCAbstractRobot;
    BCAbstractRobot["__class"] = "bc19.BCAbstractRobot";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Constants = (function () {
        function Constants() {
        }
        Constants.X_$LI$ = function () { if (Constants.X == null)
            Constants.X = [1, 0, -1, 0, 1, 1, -1, -1, 2, 0, -2, 0]; return Constants.X; };
        ;
        Constants.Y_$LI$ = function () { if (Constants.Y == null)
            Constants.Y = [0, 1, 0, -1, 1, -1, -1, 1, 0, 2, 0, -2]; return Constants.Y; };
        ;
        Constants.Steplength_$LI$ = function () { if (Constants.Steplength == null)
            Constants.Steplength = [1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4]; return Constants.Steplength; };
        ;
        Constants.LocationSize_$LI$ = function () { if (Constants.LocationSize == null)
            Constants.LocationSize = 64 * 64; return Constants.LocationSize; };
        ;
        Constants.karboCosts_$LI$ = function () { if (Constants.karboCosts == null)
            Constants.karboCosts = [0, 50, 10, 20, 25, 30]; return Constants.karboCosts; };
        ;
        Constants.fuelCosts_$LI$ = function () { if (Constants.fuelCosts == null)
            Constants.fuelCosts = [0, 200, 50, 50, 50, 50]; return Constants.fuelCosts; };
        ;
        Constants.attack_$LI$ = function () { if (Constants.attack == null)
            Constants.attack = [0, 0, 0, 10, 10, 20]; return Constants.attack; };
        ;
        Constants.range_$LI$ = function () { if (Constants.range == null)
            Constants.range = [0, 0, 0, 16, 64, 16]; return Constants.range; };
        ;
        Constants.dangerRange_$LI$ = function () { if (Constants.dangerRange == null)
            Constants.dangerRange = [0, 0, 0, 16, 64, 26]; return Constants.dangerRange; };
        ;
        Constants.minRange_$LI$ = function () { if (Constants.minRange == null)
            Constants.minRange = [0, 0, 0, 0, 16, 0]; return Constants.minRange; };
        ;
        return Constants;
    }());
    Constants.maxMapSize = 64;
    Constants.rad4Index = 12;
    Constants.rad2Index = 8;
    Constants.CASTLE = 0;
    Constants.CHURCH = 1;
    Constants.PILGRIM = 2;
    Constants.CRUSADER = 3;
    Constants.PROPHET = 4;
    Constants.PREACHER = 5;
    Constants.UNITTYPES = 6;
    Constants.RED = 0;
    Constants.BLUE = 1;
    Constants.OBJ_CASTLE = 0;
    Constants.OBJ_FUEL = 1;
    Constants.OBJ_KARBO = 2;
    Constants.MIN_SAFE_PILGRIM = 90;
    Constants.MAX_FUEL_PILGRIM = 95;
    Constants.MAX_KARBO_PILGRIM = 9;
    Constants.MAX_DIST_FOR_GATHERING = 36;
    Constants.MAX_ID = 4100;
    Constants.OCCUPIED = 2;
    Constants.PARTIALLLY_OCCUPIED = 1;
    Constants.FREE = 0;
    Constants.MAX_TURNS_REPORTING = 5;
    Constants.FUEL_MINING_RATE = 10;
    Constants.MAX_FUEL_LOST = 30;
    Constants.SAFETY_FUEL = 100;
    Constants.SAFETY_KARBO = 60;
    Constants.SENDING_TURNS = 2;
    Constants.MSG_PILGRIM = 0;
    Constants.MSG_CHURCH = 1;
    Constants.MSG_CASTLE = 2;
    Constants.MAX_CASTLES = 3;
    Constants.INF = 10000;
    Constants.RICH_KARBO = 300;
    Constants.RICH_FUEL = 1500;
    Constants.SUPER_RICH_KARBO = 500;
    Constants.SUPER_RICH_FUEL = 2500;
    Constants.MAX_CHURCHES_WAITING = 1;
    Constants.MAX_TURN_CASTLE_LOC = 5;
    Constants.MIN_TURNS_RICH = 12;
    bc19.Constants = Constants;
    Constants["__class"] = "bc19.Constants";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Symmetry = (function () {
        function Symmetry(myRobot, utils) {
            this.xSym = true;
            this.ySym = true;
            this.myRobot = null;
            this.utils = null;
            this.myRobot = myRobot;
            this.utils = utils;
        }
        Symmetry.prototype.checkMaps = function () {
            for (var i = 0; i < this.utils.dimX; ++i) {
                for (var j = 0; j < this.utils.dimY; ++j) {
                    if (j < ((this.utils.dimY - 1) / 2 | 0)) {
                        if (this.myRobot.karboniteMap[j][i] !== this.myRobot.karboniteMap[this.utils.dimY - j - 1][i]) {
                            this.ySym = false;
                            return;
                        }
                        if (this.myRobot.fuelMap[j][i] !== this.myRobot.fuelMap[this.utils.dimY - j - 1][i]) {
                            this.ySym = false;
                            return;
                        }
                        if (this.myRobot.map[j][i] !== this.myRobot.map[this.utils.dimY - j - 1][i]) {
                            this.ySym = false;
                            return;
                        }
                    }
                    if (i < ((this.utils.dimX - 1) / 2 | 0)) {
                        if (this.myRobot.karboniteMap[j][i] !== this.myRobot.karboniteMap[j][this.utils.dimX - 1 - i]) {
                            this.xSym = false;
                            return;
                        }
                        if (this.myRobot.fuelMap[j][i] !== this.myRobot.fuelMap[j][this.utils.dimX - 1 - i]) {
                            this.xSym = false;
                            return;
                        }
                        if (this.myRobot.map[j][i] !== this.myRobot.map[j][this.utils.dimX - 1 - i]) {
                            this.xSym = false;
                            return;
                        }
                    }
                }
                ;
            }
            ;
        };
        Symmetry.prototype.checkSymmetry = function () {
            if (!this.xSym || !this.ySym)
                return;
        };
        return Symmetry;
    }());
    bc19.Symmetry = Symmetry;
    Symmetry["__class"] = "bc19.Symmetry";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Micro = (function () {
        function Micro(myRobot, utils, maxMovementIndex, unitType) {
            var _this = this;
            this.addZero = false;
            if (((myRobot != null && myRobot instanceof bc19.MyRobot) || myRobot === null) && ((utils != null && utils instanceof bc19.Utils) || utils === null) && ((typeof maxMovementIndex === 'number') || maxMovementIndex === null) && ((typeof unitType === 'number') || unitType === null)) {
                var __args = Array.prototype.slice.call(arguments);
                this.myRobot = null;
                this.utils = null;
                this.maxMovementIndex = 0;
                this.turnActivated = 0;
                this.microInfoArray = null;
                this.bestMicro = null;
                this.unitType = 0;
                this.addZero = false;
                this.myRobot = null;
                this.utils = null;
                this.maxMovementIndex = 0;
                this.turnActivated = 0;
                this.microInfoArray = null;
                this.bestMicro = null;
                this.unitType = 0;
                (function () {
                    _this.myRobot = myRobot;
                    _this.utils = utils;
                    _this.maxMovementIndex = maxMovementIndex;
                    _this.turnActivated = _this.myRobot.me.turn - 1;
                    _this.unitType = unitType;
                    _this.addZero = false;
                })();
            }
            else if (((myRobot != null && myRobot instanceof bc19.MyRobot) || myRobot === null) && ((utils != null && utils instanceof bc19.Utils) || utils === null) && ((typeof maxMovementIndex === 'number') || maxMovementIndex === null) && unitType === undefined) {
                var __args = Array.prototype.slice.call(arguments);
                this.myRobot = null;
                this.utils = null;
                this.maxMovementIndex = 0;
                this.turnActivated = 0;
                this.microInfoArray = null;
                this.bestMicro = null;
                this.unitType = 0;
                this.addZero = false;
                this.myRobot = null;
                this.utils = null;
                this.maxMovementIndex = 0;
                this.turnActivated = 0;
                this.microInfoArray = null;
                this.bestMicro = null;
                this.unitType = 0;
                (function () {
                    _this.myRobot = myRobot;
                    _this.utils = utils;
                    _this.maxMovementIndex = maxMovementIndex;
                    _this.turnActivated = _this.myRobot.me.turn - 1;
                    _this.unitType = myRobot.me.unit;
                    _this.addZero = true;
                })();
            }
            else
                throw new Error('invalid overload');
        }
        Micro.prototype.getBestIndex = function () {
            if (this.turnActivated < this.myRobot.me.turn) {
                this.generateMicroArray();
                this.turnActivated = this.myRobot.me.turn;
            }
            this.bestMicro = null;
            for (var index124 = 0; index124 < this.microInfoArray.length; index124++) {
                var m = this.microInfoArray[index124];
                {
                    if (m.isEqualOrBetter(this.bestMicro))
                        this.bestMicro = m;
                }
            }
            if (this.bestMicro == null)
                return null;
            return this.bestMicro.i;
        };
        Micro.prototype.generateMicroArray = function () {
            if (this.addZero)
                this.microInfoArray = new Array(this.maxMovementIndex + 1);
            else
                this.microInfoArray = new Array(this.maxMovementIndex);
            for (var i = 0; i < this.microInfoArray.length; ++i) {
                if (i === this.maxMovementIndex) {
                    this.microInfoArray[i] = new Micro.MicroInfo(this, i, true);
                    continue;
                }
                var newX = this.myRobot.me.x + bc19.Constants.X_$LI$()[i];
                var newY = this.myRobot.me.y + bc19.Constants.Y_$LI$()[i];
                this.microInfoArray[i] = new Micro.MicroInfo(this, i, this.utils.isEmptySpaceAbsolute(newX, newY));
            }
            ;
            for (var index125 = 0; index125 < this.utils.robotsInVision.length; index125++) {
                var r = this.utils.robotsInVision[index125];
                {
                    if (this.myRobot.isVisible(r) && r.team !== this.myRobot.me.team) {
                        for (var index126 = 0; index126 < this.microInfoArray.length; index126++) {
                            var m = this.microInfoArray[index126];
                            {
                                if (m.accessible)
                                    m.update(r);
                            }
                        }
                    }
                }
            }
        };
        Micro.prototype.isSafe = function (i) {
            return this.microInfoArray[i].isEqualOrBetter(this.bestMicro);
        };
        Micro.prototype.isSafeStaying = function () {
            if (!this.addZero)
                return false;
            return this.isSafe(this.maxMovementIndex);
        };
        return Micro;
    }());
    bc19.Micro = Micro;
    Micro["__class"] = "bc19.Micro";
    (function (Micro) {
        var MicroInfo = (function () {
            function MicroInfo(__parent, i, accessible) {
                this.__parent = __parent;
                this.dmgTaken = 0;
                this.accessible = false;
                this.minRange = bc19.Constants.INF;
                this.i = 0;
                this.x = 0;
                this.y = 0;
                this.i = i;
                this.accessible = accessible;
                if (i < __parent.maxMovementIndex) {
                    this.x = __parent.myRobot.me.x + bc19.Constants.X_$LI$()[i];
                    this.y = __parent.myRobot.me.y + bc19.Constants.Y_$LI$()[i];
                }
                else {
                    this.x = __parent.myRobot.me.x;
                    this.y = __parent.myRobot.me.y;
                }
            }
            MicroInfo.prototype.isEqualOrBetter = function (m) {
                if (!this.accessible)
                    return false;
                if (m == null)
                    return true;
                if (!m.accessible)
                    return true;
                if (this.dmgTaken < m.dmgTaken)
                    return true;
                if (m.dmgTaken < this.dmgTaken)
                    return false;
                if (this.inRange()) {
                    if (!m.inRange())
                        return true;
                    return (this.minRange >= m.minRange);
                }
                if (m.inRange())
                    return false;
                return this.minRange <= m.minRange;
            };
            MicroInfo.prototype.inRange = function () {
                return this.minRange <= bc19.Constants.range_$LI$()[this.__parent.unitType];
            };
            MicroInfo.prototype.update = function (r) {
                var d = this.__parent.utils.distance$int$int$int$int(r.x, r.y, this.x, this.y);
                if (bc19.Constants.dangerRange_$LI$()[r.unit] >= d)
                    this.dmgTaken += bc19.Constants.attack_$LI$()[r.unit];
                if (this.__parent.myRobot.me.unit !== bc19.Constants.PILGRIM && this.minRange > d)
                    this.minRange = d;
            };
            return MicroInfo;
        }());
        Micro.MicroInfo = MicroInfo;
        MicroInfo["__class"] = "bc19.Micro.MicroInfo";
    })(Micro = bc19.Micro || (bc19.Micro = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var CastleCommunication = (function () {
        function CastleCommunication(myRobot) {
            this.round = -1;
            this.messageBits = 4;
            this.locBits = 64;
            this.myRobot = null;
            this.myRobot = myRobot;
            this.readRound();
        }
        CastleCommunication.prototype.update = function () {
            if (this.round === 1)
                this.round = 0;
            else if (this.round === 0)
                this.round = 1;
            if (this.round === -1) {
                this.readRound();
                if (this.round !== -1)
                    this.update();
            }
        };
        CastleCommunication.prototype.readRound = function () {
            var visibleRobots = this.myRobot.getVisibleRobots();
            for (var index127 = 0; index127 < visibleRobots.length; index127++) {
                var r = visibleRobots[index127];
                {
                    if (!this.myRobot.isVisible(r))
                        continue;
                    if (r.team === this.myRobot.me.team && r.unit === bc19.Constants.CASTLE || r.unit === bc19.Constants.CHURCH) {
                        if (this.myRobot.isRadioing(r)) {
                            this.round = ((r.signal / bc19.Constants.LocationSize_$LI$() | 0)) % 2;
                            break;
                        }
                    }
                }
            }
        };
        CastleCommunication.prototype.sendCastleMessage = function (message, target) {
            if (target == null)
                return;
            var mes = (message % this.messageBits) * this.locBits;
            if (this.round % 2 === 0)
                mes += target.x;
            else
                mes += target.y;
            this.myRobot.castleTalk(mes + 1);
        };
        return CastleCommunication;
    }());
    bc19.CastleCommunication = CastleCommunication;
    CastleCommunication["__class"] = "bc19.CastleCommunication";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Unit = (function () {
        function Unit(myRobot) {
            this.myRobot = null;
            this.myRobot = myRobot;
        }
        return Unit;
    }());
    bc19.Unit = Unit;
    Unit["__class"] = "bc19.Unit";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MineAction = (function (_super) {
        __extends(MineAction, _super);
        function MineAction(signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.action = "mine";
            return _this;
        }
        return MineAction;
    }(bc19.Action));
    bc19.MineAction = MineAction;
    MineAction["__class"] = "bc19.MineAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var ErrorAction = (function (_super) {
        __extends(ErrorAction, _super);
        function ErrorAction(error, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.error = null;
            _this.error = error.message;
            return _this;
        }
        return ErrorAction;
    }(bc19.Action));
    bc19.ErrorAction = ErrorAction;
    ErrorAction["__class"] = "bc19.ErrorAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var GiveAction = (function (_super) {
        __extends(GiveAction, _super);
        function GiveAction(giveKarbonite, giveFuel, dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.give_karbonite = 0;
            _this.give_fuel = 0;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "give";
            _this.give_karbonite = giveKarbonite;
            _this.give_fuel = giveFuel;
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return GiveAction;
    }(bc19.Action));
    bc19.GiveAction = GiveAction;
    GiveAction["__class"] = "bc19.GiveAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var BuildAction = (function (_super) {
        __extends(BuildAction, _super);
        function BuildAction(buildUnit, dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.build_unit = 0;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "build";
            _this.build_unit = buildUnit;
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return BuildAction;
    }(bc19.Action));
    bc19.BuildAction = BuildAction;
    BuildAction["__class"] = "bc19.BuildAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var TradeAction = (function (_super) {
        __extends(TradeAction, _super);
        function TradeAction(trade_fuel, trade_karbonite, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.trade_fuel = 0;
            _this.trade_karbonite = 0;
            _this.action = "trade";
            _this.trade_fuel = trade_fuel;
            _this.trade_karbonite = trade_karbonite;
            return _this;
        }
        return TradeAction;
    }(bc19.Action));
    bc19.TradeAction = TradeAction;
    TradeAction["__class"] = "bc19.TradeAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MoveAction = (function (_super) {
        __extends(MoveAction, _super);
        function MoveAction(dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "move";
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return MoveAction;
    }(bc19.Action));
    bc19.MoveAction = MoveAction;
    MoveAction["__class"] = "bc19.MoveAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var AttackAction = (function (_super) {
        __extends(AttackAction, _super);
        function AttackAction(dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "attack";
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return AttackAction;
    }(bc19.Action));
    bc19.AttackAction = AttackAction;
    AttackAction["__class"] = "bc19.AttackAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MyRobot = (function (_super) {
        __extends(MyRobot, _super);
        function MyRobot() {
            var _this = _super !== null && _super.apply(this, arguments) || this;
            _this.unit = null;
            return _this;
        }
        MyRobot.prototype.turn = function () {
            if (this.unit == null) {
                switch ((this.me.unit)) {
                    case bc19.Constants.CASTLE:
                        this.unit = new bc19.Castle(this);
                        break;
                    case bc19.Constants.CHURCH:
                        this.unit = new bc19.Church(this);
                        break;
                    case bc19.Constants.PILGRIM:
                        this.unit = new bc19.Pilgrim(this);
                        break;
                    case bc19.Constants.CRUSADER:
                        this.unit = new bc19.Crusader(this);
                        break;
                    case bc19.Constants.PROPHET:
                        this.unit = new bc19.Prophet(this);
                        break;
                    case bc19.Constants.PREACHER:
                        this.unit = new bc19.Preacher(this);
                        break;
                }
            }
            return this.unit.turn();
        };
        return MyRobot;
    }(bc19.BCAbstractRobot));
    bc19.MyRobot = MyRobot;
    MyRobot["__class"] = "bc19.MyRobot";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Crusader = (function (_super) {
        __extends(Crusader, _super);
        function Crusader(myRobot) {
            return _super.call(this, myRobot) || this;
        }
        /**
         *
         * @return {bc19.Action}
         */
        Crusader.prototype.turn = function () {
            return null;
        };
        return Crusader;
    }(bc19.Unit));
    bc19.Crusader = Crusader;
    Crusader["__class"] = "bc19.Crusader";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Pilgrim = (function (_super) {
        __extends(Pilgrim, _super);
        function Pilgrim(myRobot) {
            var _this = _super.call(this, myRobot) || this;
            _this.objective = null;
            _this.closestStructure = null;
            _this.approxFuelLostInTravel = 0;
            _this.MINING_TARGET = 0;
            _this.STRUCTURE_TARGET = 1;
            _this.beenToObjective = false;
            _this.distFromObjective = null;
            _this.fuelFromObjective = null;
            _this.utils = null;
            _this.actionToPerform = null;
            _this.targetCode = 0;
            _this.destination = null;
            _this.castleCommunication = null;
            _this.churchBuild = null;
            _this.micro = null;
            _this.utils = new bc19.Utils(myRobot);
            _this.castleCommunication = new bc19.CastleCommunication(myRobot);
            _this.churchBuild = new bc19.ChurchBuild(myRobot, _this);
            _this.micro = new bc19.Micro(myRobot, _this.utils, bc19.Constants.rad4Index);
            return _this;
        }
        /**
         *
         * @return {bc19.Action}
         */
        Pilgrim.prototype.turn = function () {
            this.utils.update();
            if (this.onObjective())
                this.beenToObjective = true;
            var safeDir = this.micro.getBestIndex();
            this.castleCommunication.update();
            if (this.objective == null)
                this.findObjective();
            if (this.objective != null)
                this.findClosestStructure();
            this.actionToPerform = null;
            this.findDestination();
            this.goToDestination();
            this.churchBuild.tryBuildChurch();
            this.sendCastleMessage();
            if (!this.micro.isSafeStaying())
                this.actionToPerform = this.myRobot.move(bc19.Constants.X_$LI$()[safeDir], bc19.Constants.Y_$LI$()[safeDir]);
            return this.actionToPerform;
        };
        Pilgrim.prototype.sendCastleMessage = function () {
            var info = bc19.Constants.MSG_PILGRIM;
            if (this.needChurch())
                info = bc19.Constants.MSG_CHURCH;
            this.castleCommunication.sendCastleMessage(info, this.objective);
        };
        Pilgrim.prototype.findDestination = function () {
            if (this.myRobot.me.fuel >= bc19.Constants.MAX_FUEL_PILGRIM || this.myRobot.me.karbonite >= bc19.Constants.MAX_KARBO_PILGRIM) {
                this.targetCode = this.STRUCTURE_TARGET;
                this.destination = this.closestStructure;
            }
            else {
                this.targetCode = this.MINING_TARGET;
                this.destination = this.objective;
            }
        };
        Pilgrim.prototype.findObjective = function () {
            for (var i = 0; i < bc19.Constants.rad2Index; ++i) {
                var newX = this.myRobot.me.x + bc19.Constants.X_$LI$()[i];
                var newY = this.myRobot.me.y + bc19.Constants.Y_$LI$()[i];
                var robot = this.utils.getRobot(newX, newY);
                if (robot != null) {
                    if (robot.team === this.myRobot.me.team && robot.unit === bc19.Constants.CASTLE && this.myRobot.isRadioing(robot)) {
                        var message = robot.signal;
                        var xObj = ((message / bc19.Constants.maxMapSize | 0)) % bc19.Constants.maxMapSize;
                        var yObj = message % bc19.Constants.maxMapSize;
                        this.objective = new bc19.Location(xObj, yObj);
                        break;
                    }
                }
            }
            ;
        };
        Pilgrim.prototype.goToDestination = function () {
            if (this.destination == null)
                return;
            var myX = this.myRobot.me.x;
            var myY = this.myRobot.me.y;
            if (this.targetCode === this.MINING_TARGET) {
                if (myX === this.destination.x && myY === this.destination.y) {
                    this.actionToPerform = this.myRobot.mine();
                    return;
                }
            }
            else if (this.targetCode === this.STRUCTURE_TARGET) {
                if (this.utils.distance$int$int$int$int(myX, myY, this.destination.x, this.destination.y) <= 2) {
                    this.actionToPerform = this.myRobot.give(this.destination.x - myX, this.destination.y - myY, this.myRobot.me.karbonite, this.myRobot.me.fuel);
                    return;
                }
            }
            var fuel = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var dirs = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var distM = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            distM[myX][myY] = 1;
            var queue = ([]);
            /* add */ (queue.push(new bc19.Location(myX, myY)) > 0);
            while ((!(queue.length == 0))) {
                var last = (function (a) { return a.length == 0 ? null : a.shift(); })(queue);
                var lastFuel = fuel[last.x][last.y];
                var lastDist = distM[last.x][last.y];
                if (this.isDestination(last)) {
                    if (this.micro.isSafe(dirs[last.x][last.y]))
                        this.actionToPerform = this.myRobot.move(bc19.Constants.X_$LI$()[dirs[last.x][last.y]], bc19.Constants.Y_$LI$()[dirs[last.x][last.y]]);
                    break;
                }
                for (var i = 0; i < bc19.Constants.rad4Index; ++i) {
                    var newX = last.x + bc19.Constants.X_$LI$()[i];
                    var newY = last.y + bc19.Constants.Y_$LI$()[i];
                    var newFuel = lastFuel + bc19.Constants.Steplength_$LI$()[i];
                    if (this.utils.isInMap(newX, newY) && this.utils.isEmptySpaceAbsolute(newX, newY)) {
                        if (distM[newX][newY] === 0) {
                            /* add */ (queue.push(new bc19.Location(newX, newY)) > 0);
                            distM[newX][newY] = lastDist + 1;
                            fuel[newX][newY] = newFuel;
                            if (lastDist === 1)
                                dirs[newX][newY] = i;
                            else
                                dirs[newX][newY] = dirs[last.x][last.y];
                        }
                        if (distM[newX][newY] === lastDist + 1) {
                            if (fuel[newX][newY] > newFuel) {
                                fuel[newX][newY] = newFuel;
                                if (lastDist === 1)
                                    dirs[newX][newY] = i;
                                else
                                    dirs[newX][newY] = dirs[last.x][last.y];
                            }
                        }
                    }
                }
                ;
            }
            ;
        };
        Pilgrim.prototype.isDestination = function (loc) {
            if (this.targetCode === this.MINING_TARGET)
                return this.utils.distance$bc19_Location$bc19_Location(loc, this.destination) === 0;
            if (this.targetCode === this.STRUCTURE_TARGET)
                return this.utils.distance$bc19_Location$bc19_Location(loc, this.destination) <= 2;
            return false;
        };
        Pilgrim.prototype.generateDistsFromObjective = function () {
            this.fuelFromObjective = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            this.distFromObjective = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            this.distFromObjective[this.objective.x][this.objective.y] = 1;
            var queue = ([]);
            /* add */ (queue.push(new bc19.Location(this.objective.x, this.objective.y)) > 0);
            while ((!(queue.length == 0))) {
                var last = (function (a) { return a.length == 0 ? null : a.shift(); })(queue);
                var lastFuel = this.fuelFromObjective[last.x][last.y];
                var lastDist = this.distFromObjective[last.x][last.y];
                for (var i = 0; i < bc19.Constants.rad4Index; ++i) {
                    var newX = last.x + bc19.Constants.X_$LI$()[i];
                    var newY = last.y + bc19.Constants.Y_$LI$()[i];
                    var newFuel = lastFuel + 2 * (bc19.Constants.Steplength_$LI$()[i] + bc19.Constants.FUEL_MINING_RATE);
                    if (this.utils.isInMap(newX, newY) && this.myRobot.map[newY][newX]) {
                        if (this.distFromObjective[newX][newY] === 0) {
                            /* add */ (queue.push(new bc19.Location(newX, newY)) > 0);
                            this.distFromObjective[newX][newY] = lastDist + 1;
                            this.fuelFromObjective[newX][newY] = newFuel;
                        }
                        if (this.distFromObjective[newX][newY] === lastDist + 1) {
                            if (this.fuelFromObjective[newX][newY] > newFuel) {
                                this.fuelFromObjective[newX][newY] = newFuel;
                            }
                        }
                    }
                }
                ;
            }
            ;
        };
        Pilgrim.prototype.approxLostFuelToChurch = function (x, y) {
            var ans = null;
            for (var i = 0; i < bc19.Constants.rad2Index; ++i) {
                var newX = x + bc19.Constants.X_$LI$()[i];
                var newY = y + bc19.Constants.Y_$LI$()[i];
                if (this.utils.isInMap(newX, newY) && this.myRobot.map[newY][newX]) {
                    if (ans == null || ans > this.fuelFromObjective[newX][newY])
                        ans = this.fuelFromObjective[newX][newY];
                }
            }
            ;
            return ans;
        };
        Pilgrim.prototype.findClosestStructure = function () {
            if (this.objective == null)
                return;
            if (this.distFromObjective == null)
                this.generateDistsFromObjective();
            for (var index128 = 0; index128 < this.utils.robotsInVision.length; index128++) {
                var r = this.utils.robotsInVision[index128];
                {
                    if (r.team !== this.myRobot.me.team)
                        continue;
                    if (r.unit === bc19.Constants.CASTLE || r.unit === bc19.Constants.CHURCH) {
                        var approxFuel = this.approxLostFuelToChurch(r.x, r.y);
                        if (approxFuel != null && (this.closestStructure == null || approxFuel < this.approxFuelLostInTravel)) {
                            this.closestStructure = new bc19.Location(r.x, r.y);
                            this.approxFuelLostInTravel = approxFuel;
                        }
                    }
                }
            }
        };
        Pilgrim.prototype.needChurch = function () {
            return !this.churchBuild.builtChurch && this.objective != null && this.approxFuelLostInTravel > bc19.Constants.MAX_FUEL_LOST;
        };
        Pilgrim.prototype.onObjective = function () {
            return this.objective != null && this.objective.x === this.myRobot.me.x && this.objective.y === this.myRobot.me.y;
        };
        return Pilgrim;
    }(bc19.Unit));
    bc19.Pilgrim = Pilgrim;
    Pilgrim["__class"] = "bc19.Pilgrim";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Castle = (function (_super) {
        __extends(Castle, _super);
        function Castle(myRobot) {
            var _this = _super.call(this, myRobot) || this;
            _this.castleUtils = null;
            _this.objective = 0;
            _this.castleUtils = new bc19.CastleUtils(myRobot);
            _this.objective = 0;
            return _this;
        }
        /**
         *
         * @return {bc19.Action}
         */
        Castle.prototype.turn = function () {
            this.castleUtils.update();
            var objective = this.castleUtils.shouldBuildPilgrim();
            if (objective != null)
                this.castleUtils.createPilgrim(objective);
            this.castleUtils.checkDefense();
            this.castleUtils.checkFreeBuild();
            return this.castleUtils.nextTurnAction;
        };
        return Castle;
    }(bc19.Unit));
    bc19.Castle = Castle;
    Castle["__class"] = "bc19.Castle";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Preacher = (function (_super) {
        __extends(Preacher, _super);
        function Preacher(myRobot) {
            var _this = _super.call(this, myRobot) || this;
            _this.micro = null;
            _this.utils = null;
            _this.utils = new bc19.Utils(myRobot);
            _this.micro = new bc19.Micro(myRobot, _this.utils, bc19.Constants.rad4Index);
            return _this;
        }
        /**
         *
         * @return {bc19.Action}
         */
        Preacher.prototype.turn = function () {
            this.utils.update();
            var attackAction = this.getAttackAction();
            if (attackAction != null)
                return attackAction;
            var bestAction = this.micro.getBestIndex();
            if (bestAction != null && bestAction !== this.micro.maxMovementIndex)
                return this.myRobot.move(bc19.Constants.X_$LI$()[bestAction], bc19.Constants.Y_$LI$()[bestAction]);
            return null;
        };
        Preacher.prototype.getAttackAction = function () {
            var bestRobot = null;
            for (var index129 = 0; index129 < this.utils.robotsInVision.length; index129++) {
                var r = this.utils.robotsInVision[index129];
                {
                    if (!this.myRobot.isVisible(r))
                        continue;
                    if (r.team === this.myRobot.me.team)
                        continue;
                    if (!this.inRange(r))
                        continue;
                    if (bestRobot == null || bestRobot.health < r.health)
                        bestRobot = r;
                }
            }
            if (bestRobot != null)
                return this.myRobot.attack(bestRobot.x - this.myRobot.me.x, bestRobot.y - this.myRobot.me.y);
            return null;
        };
        Preacher.prototype.inRange = function (r) {
            var d = this.utils.distance$int$int$int$int(r.x, r.y, this.myRobot.me.x, this.myRobot.me.y);
            if (d > bc19.Constants.range_$LI$()[this.myRobot.me.unit])
                return false;
            if (d < bc19.Constants.minRange_$LI$()[this.myRobot.me.unit])
                return false;
            return true;
        };
        return Preacher;
    }(bc19.Unit));
    bc19.Preacher = Preacher;
    Preacher["__class"] = "bc19.Preacher";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Church = (function (_super) {
        __extends(Church, _super);
        function Church(myRobot) {
            var _this = _super.call(this, myRobot) || this;
            _this.defenseMechanism = null;
            _this.utils = null;
            _this.utils = new bc19.Utils(myRobot);
            _this.defenseMechanism = new bc19.DefenseMechanism(myRobot, _this.utils);
            return _this;
        }
        /**
         *
         * @return {bc19.Action}
         */
        Church.prototype.turn = function () {
            this.utils.update();
            var act = this.defenseMechanism.defenseAction();
            if (act != null)
                return act;
            return this.defenseMechanism.buildUnitRich();
        };
        return Church;
    }(bc19.Unit));
    bc19.Church = Church;
    Church["__class"] = "bc19.Church";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Prophet = (function (_super) {
        __extends(Prophet, _super);
        function Prophet(myRobot) {
            var _this = _super.call(this, myRobot) || this;
            _this.objective = null;
            _this.distFromObjective = null;
            _this.fuelFromObjective = null;
            _this.occupied = null;
            _this.mod2 = -1;
            _this.micro = null;
            _this.utils = null;
            _this.utils = new bc19.Utils(myRobot);
            _this.micro = new bc19.Micro(myRobot, _this.utils, bc19.Constants.rad4Index);
            _this.occupied = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return undefined;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([_this.utils.dimX, _this.utils.dimY]);
            return _this;
        }
        /**
         *
         * @return {bc19.Action}
         */
        Prophet.prototype.turn = function () {
            this.utils.update();
            if (this.mod2 === -1)
                this.readMod();
            if (this.mod2 !== -1)
                this.generateDistancesFromObjective();
            var attackAction = this.getAttackAction();
            if (attackAction != null)
                return attackAction;
            var bestAction = this.micro.getBestIndex();
            if (bestAction != null && bestAction !== this.micro.maxMovementIndex)
                return this.myRobot.move(bc19.Constants.X_$LI$()[bestAction], bc19.Constants.Y_$LI$()[bestAction]);
            var act = this.moveToBestLoc();
            if (act != null)
                return act;
            return null;
        };
        Prophet.prototype.generateDistancesFromObjective = function () {
            this.fuelFromObjective = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            this.distFromObjective = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            this.distFromObjective[this.objective.x][this.objective.y] = 1;
            var queue = ([]);
            /* add */ (queue.push(new bc19.Location(this.objective.x, this.objective.y)) > 0);
            while ((!(queue.length == 0))) {
                var last = (function (a) { return a.length == 0 ? null : a.shift(); })(queue);
                var lastFuel = this.fuelFromObjective[last.x][last.y];
                var lastDist = this.distFromObjective[last.x][last.y];
                for (var i = 0; i < bc19.Constants.rad4Index; ++i) {
                    var newX = last.x + bc19.Constants.X_$LI$()[i];
                    var newY = last.y + bc19.Constants.Y_$LI$()[i];
                    var newFuel = lastFuel + 2 * (bc19.Constants.Steplength_$LI$()[i] + bc19.Constants.FUEL_MINING_RATE);
                    if (this.utils.isInMap(newX, newY) && this.myRobot.map[newY][newX]) {
                        if (this.distFromObjective[newX][newY] === 0) {
                            /* add */ (queue.push(new bc19.Location(newX, newY)) > 0);
                            this.distFromObjective[newX][newY] = lastDist + 1;
                            this.fuelFromObjective[newX][newY] = newFuel;
                        }
                        if (this.distFromObjective[newX][newY] === lastDist + 1) {
                            if (this.fuelFromObjective[newX][newY] > newFuel) {
                                this.fuelFromObjective[newX][newY] = newFuel;
                            }
                        }
                    }
                }
                ;
            }
            ;
        };
        Prophet.prototype.moveToBestLoc = function () {
            this.updateOccMatrix();
            if (this.mod2 === -1)
                return null;
            var myX = this.myRobot.me.x;
            var myY = this.myRobot.me.y;
            var bestDist = bc19.Constants.INF;
            if ((myX + myY) % 2 === this.mod2) {
                bestDist = this.distFromObjective[myX][myY];
                return null;
            }
            var bestDir = -1;
            var fuel = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var dirs = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            var distM = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.utils.dimX, this.utils.dimY]);
            distM[myX][myY] = 1;
            var queue = ([]);
            /* add */ (queue.push(new bc19.Location(myX, myY)) > 0);
            while ((!(queue.length == 0))) {
                var last = (function (a) { return a.length == 0 ? null : a.shift(); })(queue);
                var lastFuel = fuel[last.x][last.y];
                var lastDist = distM[last.x][last.y];
                if ((last.x + last.y) % 2 === this.mod2 && !this.myRobot.karboniteMap[last.y][last.x] && !this.myRobot.fuelMap[last.y][last.x]) {
                    bestDir = dirs[last.x][last.y];
                    break;
                }
                for (var i = 0; i < bc19.Constants.rad2Index; ++i) {
                    var newX = last.x + bc19.Constants.X_$LI$()[i];
                    var newY = last.y + bc19.Constants.Y_$LI$()[i];
                    var newFuel = lastFuel + bc19.Constants.Steplength_$LI$()[i];
                    if (this.utils.isInMap(newX, newY) && this.utils.isEmptySpaceAbsolute(newX, newY) && !this.occupied[newX][newY]) {
                        if (distM[newX][newY] === 0) {
                            /* add */ (queue.push(new bc19.Location(newX, newY)) > 0);
                            distM[newX][newY] = lastDist + 1;
                            fuel[newX][newY] = newFuel;
                            if (lastDist === 1)
                                dirs[newX][newY] = i;
                            else
                                dirs[newX][newY] = dirs[last.x][last.y];
                        }
                        if (distM[newX][newY] === lastDist + 1) {
                            if (fuel[newX][newY] > newFuel) {
                                fuel[newX][newY] = newFuel;
                                if (lastDist === 1)
                                    dirs[newX][newY] = i;
                                else
                                    dirs[newX][newY] = dirs[last.x][last.y];
                            }
                        }
                    }
                }
                ;
            }
            ;
            if (bestDir >= 0) {
                return this.myRobot.move(bc19.Constants.X_$LI$()[bestDir], bc19.Constants.Y_$LI$()[bestDir]);
            }
            return null;
        };
        Prophet.prototype.updateOccMatrix = function () {
            for (var i = this.myRobot.me.x - 8; i <= this.myRobot.me.x + 8; ++i) {
                for (var j = this.myRobot.me.y - 8; j <= this.myRobot.me.y + 8; ++j) {
                    if (this.utils.isInMap(i, j)) {
                        if (this.utils.robotMap[j][i] === 0)
                            this.occupied[i][j] = false;
                        else if (this.utils.robotMap[j][i] > 0) {
                            var r = this.myRobot.getRobot(this.utils.robotMap[j][i]);
                            if (r.unit !== bc19.Constants.PILGRIM)
                                this.occupied[i][j] = true;
                        }
                    }
                }
                ;
            }
            ;
        };
        Prophet.prototype.readMod = function () {
            for (var i = 0; i < bc19.Constants.rad2Index; ++i) {
                var newX = this.myRobot.me.x + bc19.Constants.X_$LI$()[i];
                var newY = this.myRobot.me.y + bc19.Constants.Y_$LI$()[i];
                var robot = this.utils.getRobot(newX, newY);
                if (robot != null) {
                    if (robot.team === this.myRobot.me.team && (robot.unit === bc19.Constants.CASTLE || robot.unit === bc19.Constants.CHURCH)) {
                        this.mod2 = (robot.x + robot.y) % 2;
                        this.objective = new bc19.Location(robot.x, robot.y);
                    }
                }
            }
            ;
        };
        Prophet.prototype.getAttackAction = function () {
            var bestRobot = null;
            for (var index130 = 0; index130 < this.utils.robotsInVision.length; index130++) {
                var r = this.utils.robotsInVision[index130];
                {
                    if (!this.myRobot.isVisible(r))
                        continue;
                    if (r.team === this.myRobot.me.team)
                        continue;
                    if (!this.inRange(r))
                        continue;
                    if (this.isBetter(r, bestRobot))
                        bestRobot = r;
                }
            }
            if (bestRobot != null) {
                return this.myRobot.attack(bestRobot.x - this.myRobot.me.x, bestRobot.y - this.myRobot.me.y);
            }
            return null;
        };
        Prophet.prototype.isBetter = function (a, b) {
            if (b == null)
                return true;
            if (this.isBetterType(a, b))
                return true;
            if (this.isBetterType(b, a))
                return false;
            return a.health < b.health;
        };
        Prophet.prototype.value = function (type) {
            switch ((type)) {
                case bc19.Constants.CHURCH:
                    return 0;
                case bc19.Constants.CASTLE:
                    return 1;
                case bc19.Constants.PILGRIM:
                    return 2;
                case bc19.Constants.CRUSADER:
                    return 3;
                case bc19.Constants.PREACHER:
                    return 4;
                case bc19.Constants.PROPHET:
                    return 5;
            }
            return -1;
        };
        Prophet.prototype.isBetterType = function (a, b) {
            return this.value(a.unit) > this.value(b.unit);
        };
        Prophet.prototype.inRange = function (r) {
            var d = this.utils.distance$int$int$int$int(r.x, r.y, this.myRobot.me.x, this.myRobot.me.y);
            if (d > bc19.Constants.range_$LI$()[this.myRobot.me.unit])
                return false;
            if (d < bc19.Constants.minRange_$LI$()[this.myRobot.me.unit])
                return false;
            return true;
        };
        return Prophet;
    }(bc19.Unit));
    bc19.Prophet = Prophet;
    Prophet["__class"] = "bc19.Prophet";
})(bc19 || (bc19 = {}));
bc19.Constants.minRange_$LI$();
bc19.Constants.dangerRange_$LI$();
bc19.Constants.range_$LI$();
bc19.Constants.attack_$LI$();
bc19.Constants.fuelCosts_$LI$();
bc19.Constants.karboCosts_$LI$();
bc19.Constants.LocationSize_$LI$();
bc19.Constants.Steplength_$LI$();
bc19.Constants.Y_$LI$();
bc19.Constants.X_$LI$();
//# sourceMappingURL=bundle.js.map
var specs = {"COMMUNICATION_BITS":16,"CASTLE_TALK_BITS":8,"MAX_ROUNDS":1000,"TRICKLE_FUEL":25,"INITIAL_KARBONITE":100,"INITIAL_FUEL":500,"MINE_FUEL_COST":1,"KARBONITE_YIELD":2,"FUEL_YIELD":10,"MAX_TRADE":1024,"MAX_BOARD_SIZE":64,"MAX_ID":4096,"CASTLE":0,"CHURCH":1,"PILGRIM":2,"CRUSADER":3,"PROPHET":4,"PREACHER":5,"RED":0,"BLUE":1,"CHESS_INITIAL":100,"CHESS_EXTRA":20,"TURN_MAX_TIME":200,"MAX_MEMORY":50000000,"UNITS":[{"CONSTRUCTION_KARBONITE":null,"CONSTRUCTION_FUEL":null,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":100,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":50,"CONSTRUCTION_FUEL":200,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":50,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":10,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":1,"STARTING_HP":10,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":20,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":9,"FUEL_PER_MOVE":1,"STARTING_HP":40,"VISION_RADIUS":36,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":10,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":25,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":2,"STARTING_HP":20,"VISION_RADIUS":64,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[16,64],"ATTACK_FUEL_COST":25,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":30,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":3,"STARTING_HP":60,"VISION_RADIUS":16,"ATTACK_DAMAGE":20,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":15,"DAMAGE_SPREAD":3}]};
var robot = new bc19.MyRobot(); robot.setSpecs(specs);